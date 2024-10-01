package com.urdzik.feature.player.domain

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.core.app.ApplicationProvider
import com.google.common.util.concurrent.ListenableFuture
import com.urdzik.feature.player.domain.manager.AudioManagerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class AudioManagerImplTest {

    private lateinit var audioManager: AudioManagerImpl
    private lateinit var context: Context
    private lateinit var packageManager: PackageManager
    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>
    private lateinit var mediaController: MediaController
    private lateinit var sessionToken: SessionToken
    private lateinit var componentName: ComponentName

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        mediaControllerFuture = mock(ListenableFuture::class.java) as ListenableFuture<MediaController>
        mediaController = mock(MediaController::class.java)

        MockitoAnnotations.openMocks(this)

        `when`(mediaControllerFuture.isDone).thenReturn(true)
        `when`(mediaControllerFuture.get()).thenReturn(mediaController)

        audioManager = AudioManagerImpl(context).apply {
            mediaControllerFuture = this@AudioManagerImplTest.mediaControllerFuture
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test play calls correct media controller methods`() = runTest {
        val index = 0
        audioManager.play(index)

        verify(mediaController).seekToDefaultPosition(index)
        verify(mediaController).playWhenReady = true
        verify(mediaController).prepare()
    }

    @Test
    fun `test pause calls correct media controller methods`() = runTest {
        audioManager.pause()

        verify(mediaController).pause()
    }

    @Test
    fun `test resume calls correct media controller methods`() = runTest {
        audioManager.resume()

        verify(mediaController).play()
    }


    @Test
    fun `test getCurrentPosition returns correct value`() = runTest {
        `when`(mediaController.currentPosition).thenReturn(1000L)

        val position = audioManager.getCurrentPosition()

        assert(position == 1000L)
    }

    @Test
    fun `test getCurrentMediaItemIndex returns correct value`() = runTest {
        `when`(mediaController.currentMediaItemIndex).thenReturn(2)

        val mediaItemIndex = audioManager.getCurrentMediaItemIndex()

        assert(mediaItemIndex == 2)
    }

    @Test
    fun `test destroy releases media controller and cancels coroutines`() = runTest {
        audioManager.destroy()

        verify(mediaControllerFuture).cancel(false)
        verify(context).stopService(any(Intent::class.java))
    }

    @Test
    fun `test skipToNextAudio calls correct media controller methods`() = runTest {
        audioManager.skipToNextAudio()

        verify(mediaController).seekToNext()
    }

    @Test
    fun `test skipToPreviousAudio calls correct media controller methods`() = runTest {
        audioManager.skipToPreviousAudio()

        verify(mediaController).seekToPrevious()
    }

    @Test
    fun `test changeSpeed sets correct speed`() = runTest {
        val speed = 1.5f
        audioManager.changeSpeed(speed)

        verify(mediaController).setPlaybackSpeed(speed)
    }
}