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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.checkAnswers.{DateExceededIncomeThresholdSummary, DateNonPAYEIncomeSummary, DateOfCapitalGainsSummary, DateOfDirectorIncomeSummary, DateOfForeignIncomeSummary, DateOfPropertyIncomeSummary, DateOfTrustIncomeSummary, HasSummaryRow, OtherReasonSummary}
import viewmodels.govuk.checkbox.*

sealed trait ReasonsToCompleteATaxReturn

object ReasonsToCompleteATaxReturn extends Enumerable.Implicits {

  case object Director extends WithName("director") with ReasonsToCompleteATaxReturn
  case object Property extends WithName("property") with ReasonsToCompleteATaxReturn
  case object ForeignIncome extends WithName("foreignIncome") with ReasonsToCompleteATaxReturn
  case object Trust extends WithName("trust") with ReasonsToCompleteATaxReturn
  case object IncomeThreshold extends WithName("incomeThreshold") with ReasonsToCompleteATaxReturn
  case object NonPAYE extends WithName("nonPAYE") with ReasonsToCompleteATaxReturn
  case object CGT extends WithName("cgt") with ReasonsToCompleteATaxReturn
  case object Other extends WithName("other") with ReasonsToCompleteATaxReturn

  val values: Seq[ReasonsToCompleteATaxReturn] = Seq(
    Director,
    Property,
    ForeignIncome,
    Trust,
    IncomeThreshold,
    NonPAYE,
    CGT,
    Other
  )

  given itemOrdering: Ordering[ReasonsToCompleteATaxReturn] = Ordering.by { item =>
    val idx = values.indexOf(item)
    if idx >= 0 then idx else Int.MaxValue
  }

  val summaryRows: Map[ReasonsToCompleteATaxReturn, HasSummaryRow] =
    values.zip(
      Seq(
        DateOfDirectorIncomeSummary,
        DateOfPropertyIncomeSummary,
        DateOfForeignIncomeSummary,
        DateOfTrustIncomeSummary,
        DateExceededIncomeThresholdSummary,
        DateNonPAYEIncomeSummary,
        DateOfCapitalGainsSummary,
        OtherReasonSummary
      )
    ).toMap

  def findNextPageFromAnswers(answers: Set[ReasonsToCompleteATaxReturn]): Option[ReasonsToCompleteATaxReturn] =
    values.find(answers)
  def findNextPageFrom(page: ReasonsToCompleteATaxReturn)(answers: Set[ReasonsToCompleteATaxReturn]): Option[ReasonsToCompleteATaxReturn] =
    val index = values.indexOf(page) + 1
    values.drop(index).find(answers)

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"reasonsToCompleteATaxReturn.${value.toString}")),
          fieldId = "value",
          index   = index,
          value   = value.toString
        )
    }

  implicit val enumerable: Enumerable[ReasonsToCompleteATaxReturn] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
