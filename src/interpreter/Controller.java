package interpreter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Optional;

public class Controller {
    @FXML
    public Button odczytBtn, zapisBtn, runBtn, srcBtn;
    public TextArea edytorTextArea, konsolaTextArea;
    public BorderPane bp;
    public TableView<Token> listaTokenow;
    public TableColumn nazwaColumn, wartoscColumn;

    public ObservableList<Token> lista;

    String source = "10 FOR B=99 TO 1 STEP -1\n" +
            "20 GOSUB 100\n" +
            "30 T$=T$+\" OF BEER ON THE WALL\"\n" +
            "40 PRINT T$;\", \";T$\n" +
            "50 PRINT \"TAKE ONE DOWN AND PASS IT AROUND, \"\n" +
            "60 IF B-1<=0 THEN PRINT \"NO MORE BOTTLES OF BEER ON THE WALL\":GOTO 80\n" +
            "70 GOSUB 200:PRINT \" OF BEER ON THE WALL\"\n" +
            "80 PRINT:NEXT\n" +
            "91 PRINT \"GO TO THE STORE AND BUY SOME MORE, 99 BOTTLES OF BEER ON THE WALL\"\n" +
            "92 END\n" +
            "100 T$=STR$(B)+\" BOTTLE\":IF B>1 THEN T$=T$+\"S\"\n" +
            "110 RETURN\n" +
            "200 A=B-1:PRINT STR$(A)+\" BOTTLE\";\n" +
            "210 IF A>1 THEN PRINT \"S\";\n" +
            "220 RETURN";

    @FXML
    private void odczytBtnHnd(ActionEvent event) {
        if(edytorTextArea.getText().trim().isEmpty()){
            otworzPlik();
        }
        else {
            Alert ostrzezenieOdczyt = new Alert(Alert.AlertType.CONFIRMATION);
            ostrzezenieOdczyt.setTitle("Potwierdzenie otwarcia pliku");
            ostrzezenieOdczyt.setHeaderText(null);
            ostrzezenieOdczyt.setContentText("Czy potwierdzasz nadpisanie aktualnego tekstu?");

            Optional<ButtonType> result = ostrzezenieOdczyt.showAndWait();
            if (result.get() == ButtonType.OK){
                edytorTextArea.clear();
                otworzPlik();
            }
        }
    }

    @FXML
    private void zapisBtnHnd(ActionEvent event) {
        if(!edytorTextArea.getText().trim().isEmpty()){
            TextInputDialog nazwaPlikuZapis = new TextInputDialog();
            nazwaPlikuZapis.setTitle("Podaj nazwę pliku");
            nazwaPlikuZapis.setHeaderText(null);
            nazwaPlikuZapis.setContentText("Proszę podać nazwę pliku:");

            Optional<String> result = nazwaPlikuZapis.showAndWait();
            if (result.isPresent() && !result.get().isEmpty()){
                zapiszPlik(result.get() + ".pp");
            }
            else {
                Alert brakNazywPliku = new Alert(Alert.AlertType.INFORMATION);
                brakNazywPliku.setTitle("Brak nazwy pliku");
                brakNazywPliku.setHeaderText(null);
                brakNazywPliku.setContentText("Aby zapisać plik, nazwa nie może być pusta");

                brakNazywPliku.showAndWait();
            }

        }
        else{
            Alert pustyEdytor = new Alert(Alert.AlertType.INFORMATION);
            pustyEdytor.setTitle("Pusty edytor");
            pustyEdytor.setHeaderText(null);
            pustyEdytor.setContentText("Aby zapisać plik, edytor nie może być pusty");

            pustyEdytor.showAndWait();
        }
    }

    @FXML
    private void edytorClearBtnHnd(ActionEvent event) {
        edytorTextArea.clear();
        konsolaTextArea.appendText("Wyczyszczono edytor" + "\r\n");
    }

    @FXML
    private void konsolaClearBtnHnd(ActionEvent event) {
        konsolaTextArea.clear();
    }

    @FXML
    private void srcBtnHnd(ActionEvent event) {
        edytorTextArea.appendText(source);
    }

    @FXML
    private void runBtnHnd(ActionEvent event) {
        //for(int i=0;i<edytorTextArea.getText().length();i++){
            //System.out.println(String.valueOf(edytorTextArea.getText().charAt(b)));
        //    konsolaTextArea.appendText(String.valueOf(edytorTextArea.getText().charAt(i))+"\r\n");
        //}

        try{
            Lexer lexer = new Lexer(edytorTextArea.getText().toUpperCase());

            lista = FXCollections.observableArrayList(lexer.getTokeny());
            nazwaColumn.setCellValueFactory(new PropertyValueFactory<Token,String>("Nazwa"));
            wartoscColumn.setCellValueFactory(new PropertyValueFactory<Token,String>("Wartosc"));
            listaTokenow.setItems(lista);

            konsolaTextArea.appendText("Wygenerowano żetony: " + lista.size() + "\n");

            Parser parser = new Parser(lexer.getTokeny(), this);
            konsolaTextArea.appendText(String.valueOf(parser.expr())+"\n");
        } catch(NieznanySymbol e){
            System.out.println("Błąd analizy leksykalnej");
            konsolaTextArea.appendText("Nierozpoznane znaki w kodzie" + "\n");
        }
    }

    private void otworzPlik(){
        FileChooser fileChooserOtworz = new FileChooser();
        fileChooserOtworz.setTitle("Otwórz plik");
        FileChooser.ExtensionFilter filterOdczyt = new FileChooser.ExtensionFilter("Pliki projektu (*.pp)", "*.pp");
        fileChooserOtworz.getExtensionFilters().add(filterOdczyt);
        File plikOtworz = fileChooserOtworz.showOpenDialog(bp.getScene().getWindow());

        if(plikOtworz != null){

            try {
                FileReader fr = new FileReader(plikOtworz);
                BufferedReader br = new BufferedReader(fr);
                String s;
                while((s=br.readLine())!=null)
                {
                    edytorTextArea.appendText(s + "\n");
                }
                fr.close();
                konsolaTextArea.appendText("Otwarto plik" + "\n");
            } catch (FileNotFoundException ex) {
                System.err.println(ex);
                konsolaTextArea.appendText("Błąd otwierania pliku" + "\n");
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }

    private void zapiszPlik(String nazwaPliku){
        FileChooser fileChooserZapisz = new FileChooser();
        fileChooserZapisz.setTitle("Zapisz plik");
        fileChooserZapisz.setInitialFileName(nazwaPliku);
        File plikZapisz = fileChooserZapisz.showSaveDialog(bp.getScene().getWindow());

        try{
            FileWriter fw = new FileWriter(plikZapisz);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(edytorTextArea.getText());
            bw.close();
            konsolaTextArea.appendText("Zapisano plik" + "\n");
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
            konsolaTextArea.appendText("Błąd zapisywania pliku" + "\n");
        }
    }

}
