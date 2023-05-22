import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Rectangle2D
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.stage.Screen

import scala.util.Random

object Particula extends JFXApp3 {

  // Les parametres de base
  val PARTICLE_RADIUS = 3
  val NUM_PARTICLES = 1000

  val random = new Random()

  // la classe Particle qui contient un cercle et une direction
  case class Particle(circle: Circle, var direction: (Int, Int))

  var screenBounds: Rectangle2D = _

  override def start(): Unit = {
    screenBounds = Screen.primary.visualBounds

    val WINDOW_WIDTH: Double = screenBounds.width
    val WINDOW_HEIGHT: Double = screenBounds.height

    // creation des particules avec des positions et des couleurs aleatoires
    val particles: Seq[Particle] = (1 to NUM_PARTICLES).map { _ =>
      val x = random.nextInt(WINDOW_WIDTH.toInt)
      val y = random.nextInt(WINDOW_HEIGHT.toInt)

      val color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
      val circle = new Circle {
        centerX = x
        centerY = y
        radius = PARTICLE_RADIUS
        fill = color
      }
      val direction = randomMooreNeighborhood()
      Particle(circle, direction)
    }

    val root = new Pane {
      children = particles.map(_.circle)
      style = "-fx-background-color: black"
    }

    // creation de la scene et de la fenetre principale
    stage = new PrimaryStage {
      title = "Particle Simulation"
      scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT)
    }

    // animation des particules et verification des collisions a chaque rafraîchissement de l'ecran
    val timer = AnimationTimer { _ =>
      particles.foreach(moveParticle)
      checkCollisions(particles)
    }
    timer.start()
  }

  // deplacement d'une particule en fonction de sa direction
  def moveParticle(particle: Particle): Unit = {
    val WINDOW_WIDTH = screenBounds.width
    val WINDOW_HEIGHT = screenBounds.height

    val (dx, dy) = particle.direction
    particle.circle.centerX = (particle.circle.centerX() + dx + WINDOW_WIDTH) % WINDOW_WIDTH
    particle.circle.centerY = (particle.circle.centerY() + dy + WINDOW_HEIGHT) % WINDOW_HEIGHT
  }

  // verification des collisions entre les particules
  def checkCollisions(particles: Seq[Particle]): Unit = {
    particles.foreach { particle1 =>
      particles.foreach { particle2 =>
        if (particle1 != particle2 && particle1.circle.intersects(particle2.circle.boundsInLocal())) {
          particle1.direction = randomMooreNeighborhood()
        }
      }
    }
  }

  // Generation d'une direction aleatoire dans le voisinage de Moore
  def randomMooreNeighborhood(): (Int, Int) = {
    val directions = Seq(
      (-1, -1), (0, -1), (1, -1), (-1, 0), (1, 0), (-1, 1), (0, 1), (1, 1)
    )
    val index = random.nextInt(directions.length)
    directions(index)
  }
}

object Main extends App {
  // lancement de l'application
  Particula.main(Array())
}
