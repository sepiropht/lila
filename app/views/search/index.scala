package views.html.search

import play.api.data.Form

import lila.api.Context
import lila.app.templating.Environment._
import lila.app.ui.ScalatagsTemplate._
import lila.common.paginator.Paginator
import lila.gameSearch.{ Query, Sorting }
import lila.user.User

import controllers.routes

object index {

  def apply(form: Form[_], paginator: Option[Paginator[lila.game.Game]] = None, nbGames: Int)(implicit ctx: Context) = {
    val commons = bits of form
    import commons._
    views.html.base.layout(
      title = trans.advancedSearch.txt(),
      moreJs = frag(
        jsTag("search.js"),
        infiniteScrollTag
      ),
      moreCss = responsiveCssTag("search"),
      openGraph = lila.app.ui.OpenGraph(
        title = s"Search in ${nbGames.localize} chess games",
        url = s"$netBaseUrl${routes.Search.index().url}",
        description = s"Search in ${nbGames.localize} chess games using advanced criterions"
      ).some
    ) {
        main(cls := "box page-small")(
          h1(trans.advancedSearch()),
          st.form(
            rel := "nofollow",
            cls := "search box__pad",
            action := routes.Search.index(),
            method := "GET"
          )(dataReqs)(
              globalError(form),
              table(
                tr(
                  th(label(trans.players(), " ", span(cls := "help")("(?)"))),
                  td(cls := "usernames")(List("a", "b").map { p =>
                    div(cls := "half")(form3.input(form("players")(p))())
                  })
                ),
                colors(hide = true),
                winner(hide = true),
                loser(hide = true),
                rating,
                hasAi,
                aiLevel,
                source,
                perf,
                mode,
                turns,
                duration,
                clockTime,
                clockIncrement,
                status,
                winnerColor,
                date,
                sort,
                analysed,
                tr(
                  th,
                  td("simple action")(
                    button(tpe := "submit", cls := "submit button")(trans.search()),
                    div(cls := "wait")(
                      spinner,
                      "Searching in ", nbGames.localize, " games"
                    )
                  )
                )
              )
            ),
          div(cls := "search_result")(
            paginator.map { pager =>
              if (pager.nbResults > 0) frag(
                div(cls := "search_status")(
                  strong(pager.nbResults.localize, " games found"), " • ",
                  a(cls := "permalink", href := routes.Search.index())("Permalink"), " • "
                ),
                div(cls := "search_infinitescroll")(
                  pagerNext(pager, np => routes.Search.index(np).url),
                  views.html.game.widgets(pager.currentPageResults)
                )
              )
              else div(cls := "search_status")(
                "No game found - ",
                a(cls := "permalink", href := routes.Search.index())("Permalink")
              )
            }
          )
        )
      }
  }
}
