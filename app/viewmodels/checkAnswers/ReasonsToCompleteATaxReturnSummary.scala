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

package viewmodels.checkAnswers

import controllers.routes
import models.ReasonsToCompleteATaxReturn.{itemOrdering, summaryRows}
import models.{CheckMode, ReasonsToCompleteATaxReturn, UserAnswers}
import pages.ReasonsToCompleteATaxReturnPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

object ReasonsToCompleteATaxReturnSummary extends HasSummaryRow:

  def rows(userAnswers: UserAnswers)(implicit messages: Messages): Seq[SummaryListRow] =
    userAnswers.get(ReasonsToCompleteATaxReturnPage) match
      case None => Seq.empty[SummaryListRow]
      case Some(setOfAnswers) =>
        summaryRows
          .collect { case (k, v) if setOfAnswers(k) => v.row(userAnswers) }
          .toSeq
          .flatten

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(ReasonsToCompleteATaxReturnPage).map {
      answers =>
        val ordered: Seq[ReasonsToCompleteATaxReturn] = answers.toSeq.sorted
        val value = ValueViewModel(
          HtmlContent(
            ordered.map {
              answer => HtmlFormat.escape(messages(s"reasonsToCompleteATaxReturn.$answer")).toString
            }
            .mkString(",<br>")
          )
        )

        SummaryListRowViewModel(
          key     = "reasonsToCompleteATaxReturn.checkYourAnswersLabel",
          value   = value,
          actions = Seq(
            ActionItemViewModel("site.change", routes.ReasonsToCompleteATaxReturnController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("reasonsToCompleteATaxReturn.change.hidden"))
          )
        )
    }
