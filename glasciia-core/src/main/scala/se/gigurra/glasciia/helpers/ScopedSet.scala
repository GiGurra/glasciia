package se.gigurra.glasciia.helpers

/**
  * Created by johan on 2016-09-19.
  */
object ScopedSet {
  def apply[A](getter: => A, setter: A => Unit, value: A)(content: => Unit): Unit = {
    val prevState = getter
    Scoped((), { setter(value); content }, setter(prevState))
  }
}
