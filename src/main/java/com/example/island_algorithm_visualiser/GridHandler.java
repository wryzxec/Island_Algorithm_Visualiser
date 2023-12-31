package com.example.island_algorithm_visualiser;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.*;

public class GridHandler extends Grid {

    private Timeline timeline;
    private KeyFrame keyframe;
    private int duration;
    private int stagger; // Ensures animation keyframes are produced in a linear fashion.
    private boolean visualizationRunning = false;
    private boolean DFS_Selected = false;
    private boolean BFS_Selected = false;
    private boolean duration1xSelected = false;
    private boolean duration2xSelected = false;
    private boolean duration4xSelected = false;
    private boolean showPerimeterSelected = false;
    private boolean showLakesSelected = false;

    public GridHandler(double width, double height, int gridSize, AnchorPane anchorPane, int[][] values, boolean[][] visited, Statistics statistics){
        super(width, height, gridSize, anchorPane, values, visited, statistics);
    }

    /*
    DFS Recursive algorithm which initializes the grid values. With each recursive call, there is a chance,
    determined by the probability, that the next recursive call is/isn't called.
    Island cells are given the value 1 whilst water cells are given the value 0.
    */
    public void generateIsland(int i, int j){
        double probability = 0.45;

        if(isOutOfBounds(i, j) || getValues()[i][j] == 1){
            return;
        }
        getValues()[i][j] = 1;

        Random rnd = new Random();
        if(rnd.nextDouble() < probability){
            generateIsland(i+1, j);
            generateIsland(i-1, j);
            generateIsland(i, j+1);
            generateIsland(i, j-1);
        }
    }

    /*
    Places 1x1 islands around the grid which then expand by the previous DFS algorithm.
    Probability is small to avoid completely filling grid.
    */
    public void populateGrid(){
        Random rnd = new Random();
        double probability = 0.025;
        for(int i = 0; i < getTilesDown(); i++){
            for(int j = 0; j < getTilesAcross(); j++){
                if(rnd.nextDouble() < probability){
                    generateIsland(i, j);
                }
            }
        }
    }

    public void createCell(int i, int j){
        Rectangle rectangle = new Rectangle(j * getCellSize(), i * getCellSize(), getCellSize(), getCellSize());
        rectangle.setId(Integer.toString(getTilesAcross()*i + j));

        if(getValues()[i][j] == 1){
            rectangle.setStyle("-fx-fill: lightyellow; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
        }
        else{
            rectangle.setStyle("-fx-fill: lightblue; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
        }
        getAnchorPane().getChildren().add(rectangle);
    }

    public void initializeGrid(){
        populateGrid();
        for(int i = 0; i < getTilesDown(); i++){
            for(int j = 0; j < getTilesAcross(); j++){
                createCell(i, j);
            }
        }
    }

    public void compute(int i, int j){
        Rectangle oldRectangle = (Rectangle) getAnchorPane().lookup(Integer.toString(getTilesAcross()*i + j));
        Rectangle newRectangle = new Rectangle(j * getCellSize(), i * getCellSize(), getCellSize(), getCellSize());
        newRectangle.setId(Integer.toString(getTilesAcross()*i + j));

        newRectangle.setStyle("-fx-fill: lightgreen; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
        getAnchorPane().getChildren().remove(oldRectangle);
        getAnchorPane().getChildren().add(newRectangle);
    }

    /*
    Shows the location of the 'iterator' as it traverses the grid. This is done by updating the current cell with
    the colour red and then restoring the previous cell with its original colour, with each iteration.
    */
    public void visualizeIteration(int i, int j){

        Rectangle oldRectangle = (Rectangle) getAnchorPane().lookup(Integer.toString(getTilesAcross()*i + j));
        Rectangle newRectangle = new Rectangle(j * getCellSize(), (i) * getCellSize(), getCellSize(), getCellSize());
        newRectangle.setId(Integer.toString(getTilesAcross()*i + j));
        newRectangle.setStyle("-fx-fill: red; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
        getAnchorPane().getChildren().remove(oldRectangle);
        getAnchorPane().getChildren().add(newRectangle);

        if(!(i == 0 && j == 0)){
            if(j != 0){
                Rectangle oldPrevRectangle = (Rectangle) getAnchorPane().lookup(Integer.toString(getTilesAcross()*i + j-1));
                Rectangle newPrevRectangle = new Rectangle((j-1) * getCellSize(), (i) * getCellSize(), getCellSize(), getCellSize());
                newPrevRectangle.setId(Integer.toString(getTilesAcross()*i + j));
                if(getValues()[i][j-1] == 1){
                    newPrevRectangle.setStyle("-fx-fill: lightgreen; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
                }
                else{
                    newPrevRectangle.setStyle("-fx-fill: lightblue; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
                }
                getAnchorPane().getChildren().remove(oldPrevRectangle);
                getAnchorPane().getChildren().add(newPrevRectangle);
            }
            else{
                Rectangle oldPrevRectangle = (Rectangle) getAnchorPane().lookup(Integer.toString(getTilesAcross()*i + getTilesAcross()-1));
                Rectangle newPrevRectangle = new Rectangle((getTilesAcross()-1) * getCellSize(), (i-1) * getCellSize(), getCellSize(), getCellSize());
                newPrevRectangle.setId(Integer.toString(getTilesAcross()*i + getTilesAcross()-1));
                if(getValues()[i-1][getTilesAcross()-1] == 1){
                    newPrevRectangle.setStyle("-fx-fill: lightgreen; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
                }
                else{
                    newPrevRectangle.setStyle("-fx-fill: lightblue; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
                }
                getAnchorPane().getChildren().remove(oldPrevRectangle);
                getAnchorPane().getChildren().add(newPrevRectangle);
            }
        }
    }

    public void visualizeMaxPerimeter(){
        List<Pair<Integer, Integer>> maxIslandPerimeterPoints = getStatistics().getMaxPerimeterPoints();

        for(Pair<Integer, Integer> pos : maxIslandPerimeterPoints){
            int i = pos.getKey();
            int j = pos.getValue();
            Rectangle oldRectangle = (Rectangle) getAnchorPane().lookup(Integer.toString(getTilesAcross()*i + j));
            Rectangle newRectangle = new Rectangle(j * getCellSize(), (i) * getCellSize(), getCellSize(), getCellSize());
            newRectangle.setStyle("-fx-fill: rgba(255,0,0,0.25); -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
            getAnchorPane().getChildren().remove(oldRectangle);
            getAnchorPane().getChildren().add(newRectangle);
        }
    }

    public void visualizeLakes(){
        List<Pair<Integer,Integer>> lakePoints = getStatistics().getLakePoints();
        for(Pair<Integer,Integer> pos : lakePoints){
            int i = pos.getKey();
            int j = pos.getValue();
            Rectangle oldRectangle = (Rectangle) getAnchorPane().lookup(Integer.toString(getTilesAcross()*i + j));
            Rectangle newRectangle = new Rectangle(j * getCellSize(), (i) * getCellSize(), getCellSize(), getCellSize());
            newRectangle.setStyle("-fx-fill: rgba(0,0,255,0.25); -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
            getAnchorPane().getChildren().remove(oldRectangle);
            getAnchorPane().getChildren().add(newRectangle);
        }
    }

    /*
    If we find the difference between the perimeter points before and after the lakes are removed we can find
    some but not all the lake points. To find the rest we can conduct a BFS search to expand over the lake area
    and add all undiscovered lake points.
    */
    public void identifyLakePointsAndRemoveLakesFromPerimeter(){
        List<Pair<Integer, Integer>> lakePoints = new ArrayList<>();
        List<Pair<Integer, Integer>> initPerimeterPoints = getStatistics().getPerimeterPoints();
        removeLakePointsFromPerimeterPoints();

        for(Pair<Integer, Integer> pos : initPerimeterPoints){
            if(!getStatistics().getPerimeterPoints().contains(pos) && !isOutOfBounds(pos.getKey(), pos.getValue())){
                lakePoints.add(pos);
            }
        }

        boolean[][] visited = new boolean[getTilesDown()][getTilesAcross()];
        List<Pair<Integer, Integer>> dirs = new ArrayList<>();
        dirs.add(new Pair<>(1,0));
        dirs.add(new Pair<>(-1,0));
        dirs.add(new Pair<>(0,1));
        dirs.add(new Pair<>(0,-1));

        Queue<Pair<Integer, Integer>> toSearch = new LinkedList<>();
        for(Pair<Integer, Integer> pos: lakePoints){
            toSearch.add(pos);
            visited[pos.getKey()][pos.getValue()] = true;
            getStatistics().addLakePoint(pos);
        }
        while(!toSearch.isEmpty()){
            Pair<Integer, Integer> pos = toSearch.poll();
            visited[pos.getKey()][pos.getValue()] = true;
            for(Pair<Integer, Integer> dir : dirs){
                int a = pos.getKey() + dir.getKey();
                int b = pos.getValue() + dir.getValue();
                Pair<Integer, Integer> nextPos = new Pair<>(a, b);
                if(!isOutOfBounds(a, b) && getValues()[a][b] == 0 && !visited[a][b]){
                    visited[a][b] = true;
                    toSearch.add(nextPos);
                    getStatistics().addLakePoint(nextPos);
                }
            }
        }
        getStatistics().setLakeArea(getStatistics().getLakePoints().size());
        getStatistics().updateLakeAreaLabel(getStatistics().getLakeArea());
    }

    /*
    Within the identified points of each island perimeter will be points which are completely surrounded by land (lakes).
    We can then locate one of the most outermost perimeter points and conduct a BFS traversal from this point.
    This will remove all (inner) lakes as the BFS traversal will not reach them.
    */
    public void removeLakePointsFromPerimeterPoints(){
        List<Pair<Integer, Integer>> withoutLakes = new ArrayList<>();
        Queue<Pair<Integer, Integer>> toSearch = new LinkedList<>();
        List<Pair<Integer, Integer>> dirs = new ArrayList<>();
        boolean[][] visited = new boolean[getTilesDown()][getTilesAcross()];

        //We must explore diagonals too as perimeter points can be joined vertically, horizontally and diagonally.
        dirs.add(new Pair<>(1,0));
        dirs.add(new Pair<>(-1,0));
        dirs.add(new Pair<>(0,1));
        dirs.add(new Pair<>(0,-1));
        dirs.add(new Pair<>(1,1));
        dirs.add(new Pair<>(1,-1));
        dirs.add(new Pair<>(-1,1));
        dirs.add(new Pair<>(-1,-1));

        Pair<Integer, Integer> start = getStatistics().getPerimeterPoints().get(0);
        if(!isOutOfBounds(start.getKey(), start.getValue())){
            withoutLakes.add(start);
        }
        toSearch.add(start);

        while(!toSearch.isEmpty()){
            Pair<Integer,Integer> pos = toSearch.poll();
            if(!isOutOfBounds(pos.getKey(), pos.getValue())){
                visited[pos.getKey()][pos.getValue()] = true;
            }
            for(Pair<Integer, Integer> dir : dirs){
                int a = pos.getKey() + dir.getKey();
                int b = pos.getValue() + dir.getValue();
                Pair<Integer, Integer> nextPos = new Pair<>(a, b);
                if(!isOutOfBounds(a, b) && getStatistics().getPerimeterPoints().contains(nextPos) && !visited[a][b]){
                    visited[a][b] = true;
                    toSearch.add(nextPos);
                    withoutLakes.add(nextPos);
                }
                if(isOutOfBounds(a, b) && getStatistics().getPerimeterPoints().contains(nextPos)){
                    getStatistics().getPerimeterPoints().remove(nextPos);
                    toSearch.add(nextPos);
                }
            }
        }
        getStatistics().setPerimeterPoints(withoutLakes);
    }

    public boolean isOutOfBounds(int i, int j){
        return i < 0 || i >= getTilesDown() || j < 0 || j >= getTilesAcross();
    }
    /*
    When determining if a cell forms part of the perimeter during a DFS/BFS search it must satisfy one of two conditions:
        - It is a water cell
        - It is out of the grid's bounds.
    We must add out of bounds points to the islands perimeter points in order to identify lakes. However,
    they must be removed after to avoid an index out of bounds error.
    */
    public void DFS(int i, int j, int startI, int startJ){
        if(isOutOfBounds(i, j)){
            stagger += duration;
            keyframe = new KeyFrame(Duration.millis((duration*(startI*getTilesAcross() + startJ) + stagger)), e -> {
                getStatistics().incrementIslandPerimeter();
                getStatistics().addPerimeterPoint(new Pair<>(i, j));
                getStatistics().setMaxIslandPerimeter(Math.max(getStatistics().getIslandPerimeter(), getStatistics().getMaxIslandPerimeter()));
                getStatistics().updateMaxIslandPerimeterLabel(getStatistics().getMaxIslandPerimeter());
            });
            timeline.getKeyFrames().add(keyframe);
            return;
        }
        if(getVisited()[i][j]){
            return;
        }
        if(getValues()[i][j] == 0){
            stagger += duration;
            keyframe = new KeyFrame(Duration.millis((duration*(startI*getTilesAcross() + startJ) + stagger)), e -> {
                if(!getStatistics().getPerimeterPoints().contains(new Pair<>(i, j))){
                    getStatistics().incrementIslandPerimeter();
                    getStatistics().addPerimeterPoint(new Pair<>(i, j));
                    getStatistics().setMaxIslandPerimeter(Math.max(getStatistics().getIslandPerimeter(), getStatistics().getMaxIslandPerimeter()));
                    getStatistics().updateMaxIslandPerimeterLabel(getStatistics().getMaxIslandPerimeter());
                }
            });
            timeline.getKeyFrames().add(keyframe);
            return;
        }

        getVisited()[i][j] = true;
        stagger += duration;
        keyframe = new KeyFrame(Duration.millis((duration*(startI*getTilesAcross() + startJ) + stagger)), e -> {
            getStatistics().incrementVisitedCount();
            getStatistics().incrementIslandArea();
            getStatistics().updateVisitedCountLabel(getStatistics().getVisitedCount());
            getStatistics().updateIslandAreaLabel(getStatistics().getIslandArea());
            compute(i,j);
        });
        timeline.getKeyFrames().add(keyframe);
        // By taking the first direction to be up (or left) we can ensure the first encountered perimeter point is always
        // on the outer edge of the island (not a lake).
        DFS(i-1, j, startI, startJ);
        DFS(i+1, j, startI, startJ);
        DFS(i, j+1, startI, startJ);
        DFS(i, j-1, startI, startJ);
    }

    public void BFS(int i, int j){
        Queue<Pair<Integer, Integer>> toSearch = new LinkedList<>();
        List<Pair<Integer, Integer>> dirs = new ArrayList<>();
        dirs.add(new Pair<>(-1,0));
        dirs.add(new Pair<>(0,-1));
        dirs.add(new Pair<>(1,0));
        dirs.add(new Pair<>(0,1));

        keyframe = new KeyFrame(Duration.millis((duration*(i*getTilesAcross() + j) + stagger)), e -> {
            getStatistics().incrementVisitedCount();
            getStatistics().incrementIslandArea();
            getStatistics().updateVisitedCountLabel(getStatistics().getVisitedCount());
            getStatistics().updateIslandAreaLabel(getStatistics().getIslandArea());
            compute(i,j);
        });
        timeline.getKeyFrames().add(keyframe);
        toSearch.add(new Pair<>(i,j));
        getVisited()[i][j] = true;
        while(!toSearch.isEmpty()){
            Pair<Integer, Integer> pos = toSearch.poll();
            for(Pair<Integer, Integer> dir : dirs){
                int a = pos.getKey() + dir.getKey();
                int b = pos.getValue() + dir.getValue();
                if(isOutOfBounds(a, b)){
                    stagger += duration;
                    keyframe = new KeyFrame(Duration.millis((duration*(i*getTilesAcross() + j) + stagger)), e -> {
                        getStatistics().incrementIslandPerimeter();
                        getStatistics().addPerimeterPoint(new Pair<>(a,b));
                        getStatistics().setMaxIslandPerimeter(Math.max(getStatistics().getIslandPerimeter(), getStatistics().getMaxIslandPerimeter()));
                        getStatistics().updateMaxIslandPerimeterLabel(getStatistics().getMaxIslandPerimeter());

                    });
                    timeline.getKeyFrames().add(keyframe);
                }
                if(!isOutOfBounds(a, b) && getValues()[a][b] == 1 && !getVisited()[a][b]){
                    getVisited()[a][b] = true;
                    stagger += duration;
                    keyframe = new KeyFrame(Duration.millis(duration * ((i*getTilesAcross()) + j) + stagger), e -> {
                        getStatistics().incrementVisitedCount();
                        getStatistics().incrementIslandArea();
                        getStatistics().updateVisitedCountLabel(getStatistics().getVisitedCount());
                        getStatistics().updateIslandAreaLabel(getStatistics().getIslandArea());
                        compute(a,b);
                    });
                    timeline.getKeyFrames().add(keyframe);
                    toSearch.add(new Pair<>(a, b));
                }
                else if(!isOutOfBounds(a, b) && getValues()[a][b] == 0 && !getVisited()[a][b]){
                    stagger += duration;
                    keyframe = new KeyFrame(Duration.millis((duration*(i*getTilesAcross() + j) + stagger)), e -> {
                        if(!getStatistics().getPerimeterPoints().contains(new Pair<>(a,b))){
                            getStatistics().incrementIslandPerimeter();
                            getStatistics().addPerimeterPoint(new Pair<>(a,b));
                            getStatistics().setMaxIslandPerimeter(Math.max(getStatistics().getIslandPerimeter(), getStatistics().getMaxIslandPerimeter()));
                            getStatistics().updateMaxIslandPerimeterLabel(getStatistics().getMaxIslandPerimeter());
                        }
                    });
                    timeline.getKeyFrames().add(keyframe);
                }
            }
        }
    }

    public void visualize(){
        timeline = new Timeline();
        visualizationRunning = true;
        if(duration1xSelected) duration = 30;
        else if(duration2xSelected) duration = 15;
        else if(duration4xSelected) duration = 7;
        stagger = duration;

        getStatistics().setLakePoints(new ArrayList<>());

        for(int i = 0; i < getTilesDown(); i++){
            for(int j = 0; j < getTilesAcross(); j++){
                int startX = i;
                int startY = j;
                keyframe = new KeyFrame(Duration.millis((duration*(i*getTilesAcross() + j) + stagger)), e -> {
                    if(!getVisited()[startX][startY]){
                        getStatistics().incrementVisitedCount();
                        getStatistics().updateVisitedCountLabel(getStatistics().getVisitedCount());
                    }
                    visualizeIteration(startX,startY);
                });
                timeline.getKeyFrames().add(keyframe);
                if(getValues()[i][j] == 1 && !getVisited()[i][j]){
                    keyframe = new KeyFrame(Duration.millis((duration*(i*getTilesAcross() + j) + stagger)), e -> {
                        getStatistics().setIslandPerimeter(0);
                        getStatistics().setPerimeterPoints(new ArrayList<>());
                        getStatistics().incrementIslandCount();
                        getStatistics().updateIslandCountLabel(getStatistics().getIslandCount());
                    });
                    timeline.getKeyFrames().add(keyframe);

                    if(DFS_Selected && !BFS_Selected){
                        DFS(i,j,startX,startY);
                        keyframe = new KeyFrame(Duration.millis((duration*(i*getTilesAcross() + j) + stagger)), e -> {
                            identifyLakePointsAndRemoveLakesFromPerimeter();
                            if(getStatistics().getIslandPerimeter() >= getStatistics().getMaxIslandPerimeter()){
                                getStatistics().setMaxPerimeterPoints(getStatistics().getPerimeterPoints());
                            }
                        });
                        timeline.getKeyFrames().add(keyframe);
                    }
                    else if(BFS_Selected && !DFS_Selected){
                        BFS(i, j);
                        keyframe = new KeyFrame(Duration.millis((duration*(i*getTilesAcross() + j) + stagger)), e -> {
                            identifyLakePointsAndRemoveLakesFromPerimeter();
                            if(getStatistics().getIslandPerimeter() >= getStatistics().getMaxIslandPerimeter()){
                                getStatistics().setMaxPerimeterPoints(getStatistics().getPerimeterPoints());
                            }
                        });
                        timeline.getKeyFrames().add(keyframe);
                    }
                }
                if(getValues()[i][j] == 0 && !getVisited()[i][j]){
                    keyframe = new KeyFrame(Duration.millis((duration*(i*getTilesAcross() + j) + stagger)), e -> {
                        getStatistics().incrementWaterArea();
                        getStatistics().updateWaterAreaLabel(getStatistics().getWaterArea());
                    });
                    timeline.getKeyFrames().add(keyframe);
                }
            }
        }
        timeline.play();
        timeline.setOnFinished(event -> {
            if(showPerimeterSelected) {
                visualizeMaxPerimeter();
            }
            if(showLakesSelected) {
                visualizeLakes();
            }
            //Removes iterator visual from the last square on the grid.
            int i = getTilesDown()-1;
            int j = getTilesAcross()-1;
            Rectangle oldRectangle = (Rectangle) getAnchorPane().lookup(Integer.toString(getTilesAcross()*i + j));
            Rectangle newRectangle = new Rectangle(j * getCellSize(), (i) * getCellSize(), getCellSize(), getCellSize());
            if (getValues()[i][j-1] == 1){
                newRectangle.setStyle("-fx-fill: lightgreen; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
            }
            else {
                newRectangle.setStyle("-fx-fill: lightblue; -fx-stroke: rgba(0,0,0,0.25); -fx-stroke-width: 1;");
            }
            getAnchorPane().getChildren().remove(oldRectangle);
            getAnchorPane().getChildren().add(newRectangle);
            visualizationRunning = false;
        });
    }

    public void resetVisited(){
        int m = getVisited().length;
        int n = getVisited()[0].length;
        setVisited(new boolean[m][n]);
    }

    /*
    To reset the grid all cells (rectangles) are cleared from the grid and new ones are created in their place.
    The value of each cell is determined by the values array.
    */
    public void resetGrid(){
        getAnchorPane().getChildren().clear();
        for(int i = 0; i < getTilesDown(); i++){
            for(int j = 0; j < getTilesAcross(); j++){
                createCell(i, j);
            }
        }
    }

    public boolean isVisualizationRunning(){
        return visualizationRunning;
    }
    public void stopVisualisation(){
        timeline.stop();
        visualizationRunning = false;
    }
    public void setBFS_Selected(boolean isSelected){ BFS_Selected = isSelected; }
    public void setDFS_Selected(boolean isSelected){ DFS_Selected = isSelected; }
    public void setDuration1xSelected(boolean isSelected){ duration1xSelected = isSelected; }
    public void setDuration2xSelected(boolean isSelected){ duration2xSelected = isSelected; }
    public void setDuration4xSelected(boolean isSelected) { duration4xSelected = isSelected; }
    public void setShowPerimeterSelected(boolean isSelected) { showPerimeterSelected = isSelected; }
    public void setShowLakesSelected(boolean isSelected) { showLakesSelected = isSelected; }
}
