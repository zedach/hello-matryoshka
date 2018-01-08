package com.ebiznext.matryoshka

import matryoshka._
import matryoshka.data._
import matryoshka.implicits._

import scalaz._

/**
*   Created by Mourad on 08/01/2018.
*/

/*
	sealed trait Expr
	final case class Word(value: String, e: Expr) extends Expr
	final case class EOS() extends Expr // END OF STRING
*/


// 1. we rewrite our recursive data structures in Higher Kinded Type
// ==> separate recursion from operations

sealed trait Expr[A]

final case class Word[A](value: String, e: A) extends Expr[A]
final case class EOS[A]() extends Expr[A] // END OF STRING

// ---

object Main extends App {

	// 2. We implement Functor of Expression
	implicit val exprF = new Functor[Expr] {
		override def map[A, B](fa: Expr[A])(f: A => B) = fa match {
			case Word(v, e) => Word[B](v, f(e))
			case EOS() => EOS[B]()
		}
	}


	// 3. We Write our transformation

	val transformToString: Algebra[Expr, String] = { // Expr[String] => String
		case Word(w, e) => s"$w $e"
		case EOS() => ""
	}

	// 4. We define an implementation of The Hello World Expr

	def helloWorldExpr[T](implicit T: Corecursive.Aux[T, Expr]): T = {
		/*
			Quote from the README: ”The .embed calls in someExpr wrap the nodes in the fixed point type. embed is generic,
			and we abstract someExpr over the fixed point type (only requiring that it has an instance of Corecursive),
			so we can postpone the choice of the fixed point as long as possible.”
		 */

		Word[T]("Hello", Word[T]("world!", EOS[T]().embed).embed).embed
	}


	// 5. We run a catamorphism on it YOLO

	val helloWorld = helloWorldExpr[Fix[Expr]].cata(transformToString)

	println(helloWorld)

	// 6. Write the inverse Transoformation (aka Coalgebra)

	val transformToExpr: Coalgebra[Expr, String] = { // String => Expr[String]
		case "" => EOS()
		case s =>
			val (h, t) = s.span(_ != ' ')
			Word(h, t.trim)
	}

	// 7. Build an Expr from String
	val welcomeExpr: Fix[Expr]  = "Welcome to Matryoshka! 😊".ana[Fix[Expr]](transformToExpr)

	println(welcomeExpr)
	println(welcomeExpr.cata(transformToString))


}




