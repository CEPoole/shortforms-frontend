/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.routes
import pages.*
import models.*
import models.ReasonsToCompleteATaxReturn.{CGT, Director, ForeignIncome, IncomeThreshold, NonPAYE, Other, Property, Trust}

@Singleton
class Navigator @Inject()():

  private val normalRoutes: Page => UserAnswers => Call =
    case WhatIsYourNamePage => _ => routes.HaveYouEverChangedYourLastNameController.onPageLoad(NormalMode)
    case HaveYouEverChangedYourLastNamePage => answer => handleLastNameFork(answer)
    case DateLastNameChangedPage => _ => routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(NormalMode)
    case WhatIsYourNationalInsuranceNumberPage => _ => routes.DateOfBirthController.onPageLoad(NormalMode)
    case DateOfBirthPage => _ => routes.DateYouMovedToAddressController.onPageLoad(NormalMode)
    // Address lookup goes here
    case DateYouMovedToAddressPage => _ => routes.DaytimeTelephoneNumberController.onPageLoad(NormalMode)
    case DaytimeTelephoneNumberPage => _ => routes.EmailAddressController.onPageLoad(NormalMode)
    case EmailAddressPage => _ => routes.ReasonsToCompleteATaxReturnController.onPageLoad(NormalMode)
    case ReasonsToCompleteATaxReturnPage => answers => handleReasonsForReturnFork(answers)
    case page: ReasonsPage[_] => answers => handleReasonsForSubjourney(page.key)(answers)
    case OtherReasonPage => answers => routes.DateOfOtherReasonController.onPageLoad(NormalMode)
    case DateOfOtherReasonPage => answers => routes.CheckYourAnswersController.onPageLoad()
    case _ => _ => routes.IndexController.onPageLoad()

  private val checkRouteMap: Page => UserAnswers => Call =
    case _ => _ => routes.CheckYourAnswersController.onPageLoad()

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)

  private def handleLastNameFork(answers: UserAnswers): Call =
    if answers.get(HaveYouEverChangedYourLastNamePage).contains(true)
    then routes.DateLastNameChangedController.onPageLoad(NormalMode)
    else routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(NormalMode)

  private def handleReasonRoutes(reason: Option[ReasonsToCompleteATaxReturn]) =
    reason.map:
      case Director => routes.DateOfDirectorIncomeController.onSubmit(NormalMode)
      case Property => routes.DateOfPropertyIncomeController.onSubmit(NormalMode)
      case ForeignIncome => routes.DateOfForeignIncomeController.onSubmit(NormalMode)
      case Trust => routes.DateOfTrustIncomeController.onSubmit(NormalMode)
      case IncomeThreshold => routes.DateExceededIncomeThresholdController.onSubmit(NormalMode)
      case NonPAYE => routes.DateNonPAYEIncomeController.onSubmit(NormalMode)
      case CGT => routes.DateOfCapitalGainsController.onSubmit(NormalMode)
      case Other => routes.OtherReasonController.onSubmit(NormalMode)
    .getOrElse(routes.CheckYourAnswersController.onPageLoad())

  private def getReasonsFrom(answers: UserAnswers): Set[ReasonsToCompleteATaxReturn] =
    answers.get(ReasonsToCompleteATaxReturnPage).getOrElse(Set.empty[ReasonsToCompleteATaxReturn])

  private def handleReasonsForReturnFork(answers: UserAnswers): Call =
    handleReasonRoutes:
      ReasonsToCompleteATaxReturn
        .findNextPageFromAnswers(getReasonsFrom(answers))

  private def handleReasonsForSubjourney(page: ReasonsToCompleteATaxReturn)(answers: UserAnswers): Call =
    handleReasonRoutes:
      ReasonsToCompleteATaxReturn
        .findNextPageFrom(page)(getReasonsFrom(answers))
