    package com.example.island_algorithm_visualiser;

    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.*;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.BorderPane;
    import java.net.URL;
    import java.util.ResourceBundle;

    public class Controller implements Initializable {

        //Initialize UI components.
        @FXML
        private BorderPane bg_pane;
        @FXML
        private AnchorPane grid_pane;
        @FXML
        private AnchorPane button_pane;
        @FXML
        private AnchorPane text_pane;
        @FXML
        private Button start_button;
        @FXML
        private Button reset_button;
        @FXML
        private Button generate_new_button;
        @FXML
        private Label island_counter_label;
        @FXML
        private Label cells_visited_label;
        @FXML
        private Label island_area_label;
        @FXML
        private Label water_area_label;
        @FXML
        private Label lake_area_label;
        @FXML
        private Label max_island_perimeter_label;
        @FXML
        private Label invalid_selection_message;
        @FXML
        private CheckBox DFS_CheckBox;
        @FXML
        private CheckBox BFS_CheckBox;
        @FXML
        private CheckBox speed_checkbox_1x;
        @FXML
        private CheckBox speed_checkbox_2x;
        @FXML
        private CheckBox speed_checkbox_4x;
        @FXML
        private CheckBox show_perimeter_checkbox;
        @FXML
        private CheckBox show_lakes_checkbox;
        @FXML
        private Slider cell_size_slider;

        private Grid grid;
        private int cellSize;
        private int[][] values;
        private boolean[][] visited;
        private GridHandler gridHandler;
        private Statistics statistics;

        public void enableSettingsChanges(){
            DFS_CheckBox.setDisable(false);
            BFS_CheckBox.setDisable(false);
            speed_checkbox_1x.setDisable(false);
            speed_checkbox_2x.setDisable(false);
            speed_checkbox_4x.setDisable(false);
            show_perimeter_checkbox.setDisable(false);
            show_lakes_checkbox.setDisable(false);
            cell_size_slider.setDisable(false);
        }
        public void disableSettingsChanges(){
            DFS_CheckBox.setDisable(true);
            BFS_CheckBox.setDisable(true);
            speed_checkbox_1x.setDisable(true);
            speed_checkbox_2x.setDisable(true);
            speed_checkbox_4x.setDisable(true);
            show_perimeter_checkbox.setDisable(true);
            show_lakes_checkbox.setDisable(true);
            cell_size_slider.setDisable(true);
        }
        //Checks to see what settings the user has selected and updates the gridHandler class with the choices.
        public void updateUserSelections(){
            gridHandler.setBFS_Selected(BFS_CheckBox.isSelected());
            gridHandler.setDFS_Selected(DFS_CheckBox.isSelected());
            gridHandler.setDuration1xSelected(speed_checkbox_1x.isSelected());
            gridHandler.setDuration2xSelected(speed_checkbox_2x.isSelected());
            gridHandler.setDuration4xSelected(speed_checkbox_4x.isSelected());
            gridHandler.setShowPerimeterSelected(show_perimeter_checkbox.isSelected());
            gridHandler.setShowLakesSelected(show_lakes_checkbox.isSelected());
            invalid_selection_message.setVisible(!DFS_CheckBox.isSelected() && !BFS_CheckBox.isSelected());
        }
        @FXML
        void startButtonClick(MouseEvent event) {
            disableSettingsChanges();
            updateUserSelections();
            if(!gridHandler.isVisualizationRunning() && (DFS_CheckBox.isSelected() || BFS_CheckBox.isSelected())){
                gridHandler.visualize();
            }
        }
        @FXML
        void resetButtonClick(MouseEvent event){
            if(gridHandler.isVisualizationRunning()){
                gridHandler.stopVisualisation();
            }
            enableSettingsChanges();
            gridHandler.resetGrid();
            gridHandler.resetVisited();
            gridHandler.resetGrid();
            statistics.resetStats();
        }

        @FXML
        void generateNewButtonClick(MouseEvent event) {
            enableSettingsChanges();
            cellSize = (int) cell_size_slider.getValue();
            if(gridHandler.isVisualizationRunning()){
                gridHandler.stopVisualisation();
            }
            statistics.resetStats();
            grid_pane.getChildren().clear();
            values = new int[(int) grid_pane.getPrefHeight() / cellSize][(int) grid_pane.getPrefWidth() / cellSize];
            visited = new boolean[(int) grid_pane.getPrefHeight() / cellSize][(int) grid_pane.getPrefWidth() / cellSize];
            gridHandler = new GridHandler(grid_pane.getPrefWidth(), grid_pane.getPrefHeight(), cellSize, grid_pane, values, visited, statistics);
            gridHandler.initializeGrid();
        }
        @FXML
        public void DFS_CheckBoxSelected(MouseEvent event){
            BFS_CheckBox.setSelected(false);
        }
        @FXML
        public void BFS_CheckBoxSelected(MouseEvent event){
            DFS_CheckBox.setSelected(false);
        }
        @FXML
        public void speed1xSelected(MouseEvent event){
            speed_checkbox_2x.setSelected(false);
            speed_checkbox_4x.setSelected(false);
        }
        @FXML
        public void speed2xSelected(MouseEvent event) {
            speed_checkbox_1x.setSelected(false);
            speed_checkbox_4x.setSelected(false);
        }
        @FXML
        public void speed4xSelected(MouseEvent event) {
            speed_checkbox_2x.setSelected(false);
            speed_checkbox_1x.setSelected(false);
        }

        @FXML
        public void cellSizeSliderReleased(MouseEvent event){
            if(cellSize != (int) cell_size_slider.getValue()){
                cellSize = (int) cell_size_slider.getValue();
                statistics.resetStats();
                grid_pane.getChildren().clear();
                values = new int[(int) grid_pane.getPrefHeight() / cellSize][(int) grid_pane.getPrefWidth() / cellSize];
                visited = new boolean[(int) grid_pane.getPrefHeight() / cellSize][(int) grid_pane.getPrefWidth() / cellSize];
                gridHandler = new GridHandler(grid_pane.getPrefWidth(), grid_pane.getPrefHeight(), cellSize, grid_pane, values, visited, statistics);
                gridHandler.initializeGrid();
            }
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            grid_pane.setStyle("-fx-background-color: lightgrey;");
            bg_pane.setStyle("-fx-background-color: lightgrey;");

            cellSize = (int) cell_size_slider.getValue();
            statistics = new Statistics(text_pane, island_counter_label, cells_visited_label, island_area_label, water_area_label, max_island_perimeter_label, lake_area_label);
            values = new int[(int) grid_pane.getPrefHeight() / cellSize][(int) grid_pane.getPrefWidth() / cellSize];
            visited = new boolean[(int) grid_pane.getPrefHeight() / cellSize][(int) grid_pane.getPrefWidth() / cellSize];
            gridHandler = new GridHandler(grid_pane.getPrefWidth(), grid_pane.getPrefHeight(), cellSize, grid_pane, values, visited, statistics);
            DFS_CheckBox.setSelected(true);
            speed_checkbox_1x.setSelected(true);
            show_perimeter_checkbox.setSelected(true);
            show_lakes_checkbox.setSelected(true);
            gridHandler.initializeGrid();
        }
    }