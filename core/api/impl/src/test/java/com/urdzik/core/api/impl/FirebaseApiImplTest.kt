import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.urdzik.core.api.contract.model.BookResponse
import com.urdzik.core.api.impl.FirebaseApiImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`

class FirebaseApiImplTest {

    private lateinit var firebaseApi: FirebaseApiImpl
    private lateinit var mockDatabaseReference: DatabaseReference
    private lateinit var mockChildReference: DatabaseReference

    @Before
    fun setUp() {
        mockDatabaseReference = mock(DatabaseReference::class.java)
        mockChildReference = mock(DatabaseReference::class.java)

        val mockFirebaseDatabase = mock(FirebaseDatabase::class.java)
        `when`(mockFirebaseDatabase.reference).thenReturn(mockDatabaseReference)

        mockStatic(FirebaseDatabase::class.java).use { mockedFirebaseDatabase ->
            mockedFirebaseDatabase.`when`<FirebaseDatabase> { FirebaseDatabase.getInstance() }
                .thenReturn(mockFirebaseDatabase)

            firebaseApi = FirebaseApiImpl()
        }

        `when`(mockDatabaseReference.child("audiobooks")).thenReturn(mockChildReference)
    }

    @Test
    fun `getBookById should return BookResponse when data is found`() = runTest {
        val testId = "test_id"
        val testBookResponse = BookResponse(id = testId)

        val mockDataSnapshot = mock(DataSnapshot::class.java)
        val mockChildSnapshot = mock(DataSnapshot::class.java)
        `when`(mockChildSnapshot.getValue(BookResponse::class.java)).thenReturn(testBookResponse)
        `when`(mockDataSnapshot.children).thenReturn(listOf(mockChildSnapshot).asIterable())

        val listenerCaptor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        doAnswer {
            val listener = it.getArgument<ValueEventListener>(0)
            listener.onDataChange(mockDataSnapshot)
            null
        }.`when`(mockChildReference).addValueEventListener(listenerCaptor.capture())

        val result = firebaseApi.getBookById(testId)

        assertEquals(testBookResponse, result)
    }

    @Test
    fun `getBookById should throw Exception when data is not found`() = runTest {
        val testId = "test_id"

        val mockDataSnapshot = mock(DataSnapshot::class.java)
        `when`(mockDataSnapshot.children).thenReturn(emptyList<DataSnapshot>().asIterable())

        doAnswer {
            val listener = it.getArgument<ValueEventListener>(0)
            listener.onDataChange(mockDataSnapshot)
            null
        }.`when`(mockChildReference).addValueEventListener(any(ValueEventListener::class.java))

        try {
            firebaseApi.getBookById(testId)
            fail("Expected an Exception to be thrown")
        } catch (e: Exception) {
            assertEquals("No data found", e.message)
        }
    }

    @Test
    fun `getBookById should throw Exception when onCancelled is called`() = runTest {
        val testId = "test_id"
        val testError = mock(DatabaseError::class.java)
        `when`(testError.toException()).thenReturn(DatabaseException("Test Exception"))

        doAnswer {
            val listener = it.getArgument<ValueEventListener>(0)
            listener.onCancelled(testError)
            null
        }.`when`(mockChildReference).addValueEventListener(any(ValueEventListener::class.java))

        try {
            firebaseApi.getBookById(testId)
            fail("Expected an Exception to be thrown")
        } catch (e: Exception) {
            assertEquals("Test Exception", e.message)
        }
    }
}