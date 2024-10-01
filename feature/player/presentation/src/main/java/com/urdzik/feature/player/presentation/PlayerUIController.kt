package com.urdzik.feature.player.presentation

import com.lowins.core.analytics.contract.MatchingAnalyticsPart
import com.lowins.core.analytics.contract.PremiumAnalyticsPart
import com.lowins.core.analytics.contract.model.EventCategory
import com.lowins.core.analytics.contract.model.Referrer
import com.lowins.core.common.CoroutineScopeLaunchWithHandlerBehaviour
import com.lowins.core.ui.ViewModelControllerContext
import com.lowins.core.ui.utils.ControllerCreationType
import com.lowins.core.ui.utils.controllerDefinition
import com.lowins.feature.final_touch.presentation.ui.ShowFinalTouchBottomSheetControllerCase
import com.lowins.feature.final_touch.presentation.ui.finalTouchBottomSheetInteractorDefinition
import com.lowins.feature.get_premium.presentation.usecase.GetPacketsUseCase
import com.lowins.feature.get_premium.presentation.usecase.GetPremiumBenefitsForGetPremiumUseCase
import com.lowins.feature.premium.data.model.ShopSubscription
import com.lowins.feature.premium.domain.usecase.GetTermsOfServiceWebViewModelUseCase
import com.lowins.feature.premium.domain.usecase.SubscribeToPremiumUseCase
import com.lowins.feature.progress.presentation.ui.ProgressWrapperController
import com.lowins.feature.progress.presentation.ui.launch
import com.lowins.feature.progress.presentation.ui.progressWrapperControllerDefinition
import com.lowins.module.user_profile_section.api.UserProfileRepository
import com.lowins.navigation.navigator.NavigateToGemShopDialogStateEventHandler
import com.lowins.navigation.navigator.NavigateToWebViewDialogStateEventHandler
import com.lowins.navigation.navigator.navigateToGemShopDialogStateEventHandlerDefinition
import com.lowins.navigation.navigator.navigateToHasPremiumStateEventHandlerDefinition
import com.lowins.navigation.navigator.navigateToWebViewDialogStateEventHandlerDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import org.koin.core.module.dsl.new
import org.koin.dsl.ScopeDSL

fun ScopeDSL.getPremiumUIControllerDefinition() = controllerDefinition(
    getContext = {
        new(::GetPremiumUIControllerContext)
    },
    getController = {
        new(::GetPremiumUIController)
    },
    controllerCreationType = ControllerCreationType.Factory,
    creationDefinitions = {
        progressWrapperControllerDefinition()

        finalTouchBottomSheetInteractorDefinition()

        navigateToHasPremiumStateEventHandlerDefinition()
        navigateToGemShopDialogStateEventHandlerDefinition()
        navigateToWebViewDialogStateEventHandlerDefinition()
    }
)

fun interface GetPremiumGoBackControllerSideEffectCase {

    fun goBack()
}

fun interface GetPremiumShowHasPremiumControllerSideEffectCase {

    fun showHasPremiumBottomSheet()
}

class GetPremiumUIControllerContext(
    viewModelControllerContext: ViewModelControllerContext,
    showFinalTouchBottomSheetControllerCase: ShowFinalTouchBottomSheetControllerCase,
    getPremiumGoBackControllerSideEffectCase: GetPremiumGoBackControllerSideEffectCase,
    getPremiumShowHasPremiumControllerSideEffectCase: GetPremiumShowHasPremiumControllerSideEffectCase
) :
    ViewModelControllerContext by viewModelControllerContext,
    ShowFinalTouchBottomSheetControllerCase by showFinalTouchBottomSheetControllerCase,
    GetPremiumGoBackControllerSideEffectCase by getPremiumGoBackControllerSideEffectCase,
    GetPremiumShowHasPremiumControllerSideEffectCase by getPremiumShowHasPremiumControllerSideEffectCase

class GetPremiumUIController(
    private val navArgs: GetPremiumNavArgs,
    private val context: GetPremiumUIControllerContext,
    private val userProfileRepository: UserProfileRepository,
    private val analyticsPart: MatchingAnalyticsPart,
    private val getPacketsUseCase: GetPacketsUseCase,
    private val progressInteractor: ProgressWrapperController,
    private val getPremiumBenefitsForGetPremiumUseCase: GetPremiumBenefitsForGetPremiumUseCase,
    private val subscribeToPremiumUseCase: SubscribeToPremiumUseCase,
    private val getTermsOfServiceWebViewModelUseCase: GetTermsOfServiceWebViewModelUseCase,
    private val premiumAnalyticsPart: PremiumAnalyticsPart,
    private val navigateToGemShopDialogStateEventHandler: NavigateToGemShopDialogStateEventHandler,
    private val navigateToWebViewDialogStateEventHandler: NavigateToWebViewDialogStateEventHandler,
) : CoroutineScopeLaunchWithHandlerBehaviour {

    private val referrerOrBlank get() = navArgs.referrer ?: ""

    private val startingCentralPacketIndex = 0

    private val _uiState = MutableStateFlow(
        GetPremiumUiState(
            premiumBenefits = getPremiumBenefitsForGetPremiumUseCase.invoke(
                ShopSubscription.week
            ),
            gemsCount = userProfileRepository.balanceChannel.value,
            packets = getPacketsUseCase.invoke(),
            centralPacketIndex = startingCentralPacketIndex,
            initialPremiumBenefitIndex = navArgs.benefit.ordinal,
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        val userId = userProfileRepository.getUserProfile()?.id.toString()
        premiumAnalyticsPart.logPageViewNewPremium(
            referrer = referrerOrBlank,
            productId = ShopSubscription.week.productId
        )

        premiumAnalyticsPart.logPageViewPayingMenu(
            referrer = referrerOrBlank,
            type = "new_premium",
            gemCount = (userProfileRepository.getUserProfile()?.gems ?: 0).toString(),
        )

        context.coroutineScope.launch {
            userProfileRepository.balanceChannel
                .drop(1)
                .collectLatest {
                    _uiState.update { uiStateValue ->
                        uiStateValue.copy(
                            gemsCount = it
                        )
                    }
                }
        }
    }

    fun obtainEvent(event: GetPremiumEvent) {
        when (event) {
            GetPremiumEvent.Back -> {
                premiumAnalyticsPart.logClickNewPremiumClose()
                context.goBack()
            }

            GetPremiumEvent.Continue -> {
                val productId = uiState.value.run { packets[centralPacketIndex] }.id
                premiumAnalyticsPart.logClickNewPremiumContinue(
                    referrer = referrerOrBlank,
                    productId = productId
                )
                progressInteractor.showProgress()

                context.coroutineScope
                    .launch {
                        progressInteractor.launch {
                            val success = subscribeToPremiumUseCase.invoke(productId, referrerOrBlank)
                            if (success) {
                                context.showHasPremiumBottomSheet()
                            } else {
                                context.showFinalTouchBottomSheet(Referrer.GET_PREMIUM.value)
                            }
                        }
                    }
            }

            GetPremiumEvent.OpenGemsShop -> {
                analyticsPart.logClickPayingMenu(EventCategory.Matching)
                navigateToGemShopDialogStateEventHandler.trigger(Referrer.GET_PREMIUM)
            }

            is GetPremiumEvent.SendCentralPacketIndex -> {
                _uiState.update { uiStateValue ->
                    uiStateValue.copy(
                        centralPacketIndex = event.index,
                        premiumBenefits = getPremiumBenefitsForGetPremiumUseCase.invoke(event.index)
                    )
                }
            }

            is GetPremiumEvent.SelectPacket -> {
                if (uiState.value.centralPacketIndex == event.index) {
                    obtainEvent(GetPremiumEvent.Continue)
                }
            }


            GetPremiumEvent.OpenTermsOfService -> {
                navigateToWebViewDialogStateEventHandler.trigger(getTermsOfServiceWebViewModelUseCase.invoke())
            }
        }
    }
}