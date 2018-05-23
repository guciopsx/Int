package interpreter;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
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
    public Button odczytBtn, zapisBtn, runBtn;
    public TextArea edytorTextArea, konsolaTextArea;
    public BorderPane bp;
    public TableView listaTokenow;
    public ListView listaTokenow1;
    public TableColumn znakColumn, wartoscColumn;

    protected ListProperty<Token> listProperty = new SimpleListProperty<>();

    public ObservableList<Token> lista;



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
    private void runBtnHnd(ActionEvent event) {
        for(int i=0;i<edytorTextArea.getText().length();i++){
            //System.out.println(String.valueOf(edytorTextArea.getText().charAt(b)));
            konsolaTextArea.appendText(String.valueOf(edytorTextArea.getText().charAt(i))+"\r\n");
        }

        Lexer lexer = new Lexer(edytorTextArea.getText());

        for(int i=0;i<lexer.getTokeny().size();i++){
            Token token = lexer.getTokeny().get(i);
            System.out.println(token.getNazwa() +" "+ token.getWartosc());
            //listaTokenow.getItems().add(token);
        }

        //TableColumn typ = new TableColumn("Typ");
        //TableColumn wartosc = new TableColumn("Wartość");


        lista = FXCollections.observableArrayList(lexer.getTokeny());
        //lista.addAll(lexer.getTokeny());
        //listaTokenow.getItems().setAll(lista);
        //listaTokenow.refresh()
        listaTokenow.setItems(lista);









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
                    edytorTextArea.appendText(s + "\r\n");
                }
                fr.close();
                konsolaTextArea.appendText("Otwarto plik" + "\r\n");
            } catch (FileNotFoundException ex) {
                System.err.println(ex);
                konsolaTextArea.appendText("Błąd otwierania pliku" + "\r\n");
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
            konsolaTextArea.appendText("Zapisano plik" + "\r\n");
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
            konsolaTextArea.appendText("Błąd zapisywania pliku" + "\r\n");
        }
    }

}
