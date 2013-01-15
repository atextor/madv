package de.atextor.madv.engine

/*
abstract class Entity {
  def draw
}

class UnidirectionalEntity {
  
}

sealed trait Direction
case object Up    extends Direction
case object Left  extends Direction
case object Down  extends Direction
case object Right extends Direction

object Direction {
  def all: List[Direction] = List(Up, Left, Down, Right)
}

sealed abstract class AnimationType(val frames: Int, val delay: Int, val directions: List[Direction])
case object Walk   extends AnimationType(frames = 9,  delay = 100, directions = Direction.all)
case object Cast   extends AnimationType(frames = 7,  delay = 200, directions = Direction.all)
case object Jab    extends AnimationType(frames = 8,  delay = 200, directions = Direction.all)
case object Stab   extends AnimationType(frames = 6,  delay = 200, directions = Direction.all)
case object Attack extends AnimationType(frames = 13, delay = 200, directions = Direction.all)
case object Die    extends AnimationType(frames = 6,  delay = 200, directions = Nil)

abstract sealed class Sprite(name: String) {
  val spriteSheet = new PackedSpriteSheet("res/sprites/" + name + ".def")
  def apply() = spriteSheet
}

case object Female                 extends Sprite("female")
case object FemaleChainMail        extends Sprite("femalechainmail")
case object FemaleChainTabard      extends Sprite("femalechaintabard")
case object FemaleDark             extends Sprite("femaledark")
case object FemaleLeatherBracers   extends Sprite("femaleleatherbracers")
case object FemaleLeatherShoulders extends Sprite("femaleleathershoulders")
case object FemaleLeatherTorso     extends Sprite("femaleleathertorso")
case object FemaleOverSkirt        extends Sprite("femaleoverskirt")
case object FemalePlateBoots       extends Sprite("femaleplateboots")
case object FemalePlateGloves      extends Sprite("femaleplategloves")
case object FemalePlateGreaves     extends Sprite("femaleplategreaves")
case object FemalePlateHelmet      extends Sprite("femaleplatehelmet")
case object FemalePlateMail        extends Sprite("femaleplatemail")
case object FemalePlateSaulders    extends Sprite("femaleplatesaulders")
case object FemaleTanned           extends Sprite("femaletanned")
case object FemaleTanned2          extends Sprite("femaletanned2")
case object FemaleUnderDress       extends Sprite("femaleunderdress")
case object FemaleVest             extends Sprite("femalevest")


class SpriteLayer(sprite: Sprite, supportedAnimations: List[AnimationType]) {
  private def actionName(d: Direction, a: AnimationType) =
    if (a.directions contains d) {
      "%s-%s".format(a.toString.toLowerCase, d.toString.toLowerCase) 
    } else {
      a.toString.toLowerCase
    }
  
  def getAnimation(d: Direction, a: AnimationType) = animations.get(actionName(d, a)).get
                       
  val animations = (for (d <- Direction.all;
                          a <- supportedAnimations;
                          if a.directions contains d) yield {
    val action = actionName(d, a)
    val ani = new Animation
    val sheet = sprite().getSpriteSheet(action)
    for (x <- 0 until a.frames) ani.addFrame(sheet.getSprite(x, 0), a.delay)
    (action -> ani)
  }).toMap
}

object SpriteLayer {
  val female                  = new SpriteLayer(Female, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleChainMail         = new SpriteLayer(FemaleChainMail, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleChainTabard       = new SpriteLayer(FemaleChainTabard, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleDark              = new SpriteLayer(FemaleDark, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleLeatherBracers    = new SpriteLayer(FemaleLeatherBracers, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleLeatherShoulders  = new SpriteLayer(FemaleLeatherShoulders, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleLeatherTorso      = new SpriteLayer(FemaleLeatherTorso, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleOverSkirt         = new SpriteLayer(FemaleOverSkirt, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femalePlateBoots        = new SpriteLayer(FemalePlateBoots, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femalePlateGloves       = new SpriteLayer(FemalePlateGloves, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femalePlateGreaves      = new SpriteLayer(FemalePlateGreaves, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femalePlateHelmet       = new SpriteLayer(FemalePlateHelmet, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femalePlateMail         = new SpriteLayer(FemalePlateMail, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femalePlateSaulders     = new SpriteLayer(FemalePlateSaulders, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleTanned            = new SpriteLayer(FemaleTanned, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleTanned2           = new SpriteLayer(FemaleTanned2, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleUnderDress        = new SpriteLayer(FemaleUnderDress, List(Walk, Cast, Jab, Stab, Attack, Die))
  val femaleVest              = new SpriteLayer(FemaleVest, List(Walk, Cast, Jab, Stab, Attack, Die))
}

class CreatureDescription(spriteLayers: List[SpriteLayer]) {
  val animationTypes = List(Walk, Cast, Jab, Stab, Attack, Die)
  def animations(d: Direction, a: AnimationType) = spriteLayers.map(_.getAnimation(d, a))
}

object EntityDescriptions {
  val playerDescription = new CreatureDescription(
      SpriteLayer.female ::
      SpriteLayer.femaleVest ::
//      SpriteLayer.femaleUnderDress ::
      SpriteLayer.femaleLeatherTorso ::
      SpriteLayer.femaleOverSkirt ::
//      SpriteLayer.femalePlateBoots ::
//      SpriteLayer.femaleLeatherBracers ::
//      SpriteLayer.femaleLeatherShoulders ::
      Nil)
}

case class MultidirectionalEntity(val description: CreatureDescription, val direction: Direction = Down) extends Entity {
  def changeDirection(newDirection: Direction) = copy(direction = newDirection)
  
  def draw {
    description.animations(direction, Walk).foreach(_.draw(168, 50))
  }
}
*/