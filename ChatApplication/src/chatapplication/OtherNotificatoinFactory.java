package chatapplication;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class OtherNotificatoinFactory implements Callback<ListView<String>, ListCell<String>> {

    @Override
    public ListCell<String> call(ListView<String> param) {
        return new ListCell<String>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                HBox notificationBox=new HBox();
                Label notification=new Label();
                
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setGraphic(null);
                }
            }
        };
    }

}
