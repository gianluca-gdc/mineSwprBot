import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.event.InputEvent
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds
import java.awt.MouseInfo
import javax.swing.JOptionPane
class Mouse {
    private val pointer: Robot = Robot()
    fun move(x:Int, y:Int){
        pointer.mouseMove(x,y)
    }
    fun placeFlag(){
        pointer.mousePress(InputEvent.BUTTON3_DOWN_MASK)
        Thread.sleep(50)
        pointer.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)

    }
    fun placeOpen(){
        pointer.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(50)
        pointer.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)

    }
}
open class Square(x:Int, y:Int){
    val xposition = x
    val yposition = y
    open val color: Int = 0
    open val numOfBombs: Int = 0
}
class Bomb1(x:Int, y: Int): Square(x,y){
    override val color : Int = -16596993
    override val numOfBombs = 1
}
class Bomb2(x:Int, y: Int): Square(x,y){
    override val color : Int = -8939723
    override val numOfBombs = 2
}
class Bomb3(x:Int, y: Int): Square(x,y){
    override val color : Int = -3462512
    override val numOfBombs = 3
}
class Bomb4(x:Int, y: Int): Square(x,y){
    override val color : Int = -3026441
    override val numOfBombs = 4
}
class Bomb5(x:Int, y: Int): Square(x,y){
    override val color : Int = -65536
    override val numOfBombs = 5
}
class Bomb6(x:Int, y: Int): Square(x,y){
    override val color : Int = -16744448
    override val numOfBombs = 6
}
class Bomb7(x:Int, y: Int): Square(x,y){
    override val color : Int = -1211922
    override val numOfBombs = 7
}
class Bomb8(x:Int, y: Int): Square(x,y){
    override val color : Int = -7667573
    override val numOfBombs = 8
}
class Unopened(x:Int, y: Int): Square(x,y){
    override val color : Int = -10185236
}
class Opened(x:Int, y: Int): Square(x,y){
    override val color : Int = -657931
}
fun getAdjustedMouseCoordinates(): Pair<Int, Int> {
    val point = MouseInfo.getPointerInfo().location
    return point.x to point.y

}
fun main(){
    println("Move your mouse to the first target location and press OK.")
    javax.swing.JOptionPane.showMessageDialog(null, "Move mouse and click OK")
    val(firstx,firsty)  = getAdjustedMouseCoordinates()
    println("Move your mouse to the second target location and press OK.")
    JOptionPane.showMessageDialog(null, "Move mouse to SECOND area and press OK")
    val(secondx,secondy)  = getAdjustedMouseCoordinates()

    val screenPixelX: Int = firstx * 2 //998
    val screenPixelWidth = (secondx * 2) - screenPixelX //1028
    val screenPixelY: Int = firsty * 2  //604
    val screenPixelHeight = (secondy*2) - screenPixelY //1026
    println("$screenPixelX, $screenPixelY, $screenPixelWidth, $screenPixelHeight")
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    print(screenSize)
    val mouse = Mouse()
    //val screenSize = Toolkit.getDefaultToolkit().screenSize
    //val captureRect = captureScreenRegion(x, y, width, height)
    println("Please Enter # of Mines: ")
    var minesRemaining: Int = readlnOrNull()!!.toInt()
    var tmp = 0
    println("Please Enter Height: ")
    val height: Int = readlnOrNull()!!.toInt()
    println("Please Enter Width: ")
    val width: Int = readlnOrNull()!!.toInt()
    val squareWidth = screenPixelWidth / width //should roughly be 64.04 for a 16x16 square
    val squareHeight = screenPixelHeight / height //should roughly be 64.04 for a 16x16 square
    var x = 0
    var y = 0
    var firstPass = true
    val listOfFlags: MutableList<IntArray> = mutableListOf()
    var iterationsThruBoard = 0
    fun placeFlag(x: Int,y :Int){
        mouse.move(x, y)
        mouse.placeFlag()
        val flagCoordinates = intArrayOf(x,y)
        listOfFlags.add(flagCoordinates)
        minesRemaining--
        //println("minesRemaining: $minesRemaining")
    }
    fun placeOpen(x:Int, y:Int){
        mouse.move(x, y)

        mouse.placeOpen()
    }
    fun checkSurroundingBlocks(squareX:Int, squareY:Int, squaresToFind: Square, image: BufferedImage):Map<Int,IntArray>{//checks around given square
        var count: Int = 0
        val mutableMap : MutableMap<Int, IntArray> = mutableMapOf()
        for(x in -squareWidth/2 .. squareWidth/2 step squareWidth/2){
            for(y in -squareHeight/2 .. squareHeight/2 step squareHeight/2){
                val currentX = squareX + x
                val currentY = squareY + y
                val pos: IntArray = intArrayOf(currentX,currentY)
                if((listOfFlags.any { it contentEquals pos })||((currentX != squareX || currentY != squareY) && image.getRGB(currentX, currentY) == squaresToFind.color)){
                    println("in list of flags: $currentX, $currentY")
                        mutableMap[count] = pos
                        count++

                }
            }

        }
        return mutableMap
    }
    fun centerOfSquare(square:Square):IntArray{
        val centerOfSquareX = (x) + (squareWidth / 2)
        val centerOfSquareY = (y) + (squareHeight / 2)
        return intArrayOf(centerOfSquareX, centerOfSquareY)
    }
    fun moveToNextSquare(){
        if (y <= screenPixelHeight && x < screenPixelWidth - squareWidth -5) {
            x += squareWidth
        }else if (x >= screenPixelWidth - squareWidth -5 && y < screenPixelHeight - squareHeight -5) {
            x = 0
            y += squareHeight
        }else{
            x = 0
            y = 0
        }
    }
    fun placeFlagsAroundSurroundingBlocks(x: Int, y: Int, bombSquare: Square, image: BufferedImage) {
        val mapOfSurrounding = checkSurroundingBlocks(x, y, Unopened(0, 0), image)
        val surroundingFlagCount:MutableList<IntArray> = mutableListOf()
        println(mapOfSurrounding.size)
        for (i in mapOfSurrounding) {
            if (listOfFlags.any { it contentEquals intArrayOf(i.value[0], i.value[1]) }) {
                //println("flag already added previously")
                surroundingFlagCount.add(intArrayOf(i.value[0], i.value[1]))
            }
        }
        if(surroundingFlagCount.size == bombSquare.numOfBombs) {
            for (i in mapOfSurrounding) {
                if (listOfFlags.any { it contentEquals intArrayOf(i.value[0], i.value[1]) }) {
                    //println("flag already added previously")

                } else {
                    //println("flag x = ${i.value[0]} , flag y = ${i.value[1]}")
                    placeOpen(i.value[0], i.value[1])
                }
            }
        }else if (mapOfSurrounding.size == bombSquare.numOfBombs) {
            //println("just ${bombSquare.numOfBombs} is unopened")
                //if only 1 is unopened, we can place Flag- It's a BOMB!!!

            for (i in mapOfSurrounding) {
                if(listOfFlags.any { it contentEquals intArrayOf(i.value[0],i.value[1]) }){
                    //println("flag already added previously")

                }else {
                    //println("flag x = ${i.value[0]} , flag y = ${i.value[1]}")
                    placeFlag(i.value[0], i.value[1])
                }
            }

        }
    }

    runBlocking {
        delay(4.seconds)
        while (minesRemaining > 0) {
            //currentSquare is the current square as we iterate thru the board to each square using x & y
            iterationsThruBoard++
            var currentSquare: Square = Square(x, y)
            var centerOfSquareX: Int = (screenPixelX + centerOfSquare(currentSquare)[0])
            var centerOfSquareY: Int = (screenPixelY + centerOfSquare(currentSquare)[1])
            val captureRect = Rectangle((screenSize.width * 2),(screenSize.height * 2))
            val screenCapture = Robot().createScreenCapture(captureRect)
            var pixelColorInt = screenCapture.getRGB(centerOfSquareX/2, centerOfSquareY/2)

            if (firstPass) {
                runBlocking {
                    placeOpen(screenPixelWidth / 2, screenPixelHeight / 2)
                    delay(50)

                }
                firstPass = false
            }
            if(iterationsThruBoard == 1){
                tmp = minesRemaining
            }
            else if(iterationsThruBoard >= (width * height)){
                //println("made it into this else if")
                if(tmp == minesRemaining){
                    while(tmp==minesRemaining){
                       //println("made it into this while loop")
                        currentSquare= Square(x, y)
                        centerOfSquareX = (screenPixelX + centerOfSquare(currentSquare)[0])
                        centerOfSquareY = (screenPixelY + centerOfSquare(currentSquare)[1])
                        pixelColorInt = screenCapture.getRGB(centerOfSquareX/2, centerOfSquareY/2)
                        if(pixelColorInt == Unopened(0,0).color){
                            placeOpen(centerOfSquareX/2,centerOfSquareY/2)
                            tmp = 0
                            iterationsThruBoard = 0
                            moveToNextSquare()
                        }else{
                            moveToNextSquare()
                        }
                    }
                }else{
                    iterationsThruBoard = 0
                    tmp = minesRemaining
                }
            }
            if (listOfFlags.contains(intArrayOf(centerOfSquareX, centerOfSquareY))) {
                moveToNextSquare()
            }
            else if(pixelColorInt == Opened(0, 0).color ){
                //mouse.move(centerOfSquareX/2,centerOfSquareY/2)
                moveToNextSquare()
            }
            else {
                fun checkBombWithSurrounding(bomb: Square) {
                    if (pixelColorInt == bomb.color) {
                        placeFlagsAroundSurroundingBlocks(centerOfSquareX/2, centerOfSquareY/2, bomb, screenCapture)

                    }
                }
                // check surrounding blocks around given square to see if any are unopened,
                // returns map of how many unopened and corresponding coordinates
                if (pixelColorInt != Unopened(0, 0).color) {
                    checkBombWithSurrounding(Bomb1(0, 0))
                    checkBombWithSurrounding(Bomb2(0, 0))
                    checkBombWithSurrounding(Bomb3(0, 0))
                    checkBombWithSurrounding(Bomb4(0, 0))
                    checkBombWithSurrounding(Bomb5(0, 0))
                    checkBombWithSurrounding(Bomb6(0, 0))
                    checkBombWithSurrounding(Bomb7(0, 0))
                    checkBombWithSurrounding(Bomb8(0, 0))
                    //mouse.move(centerOfSquareX/2, centerOfSquareY/2)
                    moveToNextSquare()
                } else {
                    //mouse.move(centerOfSquareX/2, centerOfSquareY/2)
                    moveToNextSquare()
                }
            }
        }
        //val image = ImageIO.read(File("/Users/gianluca/Downloads/jobAppMeme.jpg"))
        //val icon = ImageIcon(image)
        delay(1.seconds)
        // Show dialog with custom icon
        JOptionPane.showMessageDialog(
            null,
            "Congratulations You've Won!",
            "Winner Winner!",
            JOptionPane.INFORMATION_MESSAGE,
            null
        )


    }

}

