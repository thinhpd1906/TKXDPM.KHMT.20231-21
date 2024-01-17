package views.screen.home;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import common.exception.ViewCartException;
import controller.BaseController;
import controller.HomeController;
import controller.ViewCartController;
import entity.cart.Cart;
import entity.media.Media;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import utils.Configs;
import utils.Utils;
import views.screen.BaseScreenHandler;
import views.screen.admin.AdminScreenHandler;
import views.screen.cart.CartScreenHandler;


public class HomeScreenHandler extends BaseScreenHandler implements Initializable{

    public static Logger LOGGER = Utils.getLogger(HomeScreenHandler.class.getName());

    @FXML private Label numMediaInCart;

    @FXML private ImageView aimsImage;

    @FXML private ImageView cartImage;
    
    @FXML private ImageView adminImage;
    
    @FXML private VBox vboxMedia1;

    @FXML private VBox vboxMedia2;

    @FXML private VBox vboxMedia3;

    @FXML private HBox hboxMedia;

    @FXML private SplitMenuButton splitMenuBtnSearch;

    @FXML private TextField myTextField;

    @FXML
    private Button BtnSort;

    private List homeItems;
    private List<MediaHandler> filteredItemsSort;
    private List<Integer> priceItems;

    public HomeScreenHandler(Stage stage, String screenPath) throws IOException{
        super(stage, screenPath);
    }

    public Label getNumMediaCartLabel(){
        return this.numMediaInCart;
    }

    public HomeController getBController() {
        return (HomeController) super.getBController();
    }

    @Override
    public void show() {
        numMediaInCart.setText(String.valueOf(Cart.getCart().getListMedia().size()) + " media");
        super.show();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        setBController(new HomeController());
        try{
            List medium = getBController().getAllMedia();
            this.homeItems = new ArrayList<>();
            for (Object object : medium) {
                Media media = (Media)object;
                MediaHandler m1 = new MediaHandler(Configs.HOME_MEDIA_PATH, media, this);
                this.homeItems.add(m1);
            }
        }catch (SQLException | IOException e){
            LOGGER.info("Errors occured: " + e.getMessage());
            e.printStackTrace();
        }
        
        aimsImage.setOnMouseClicked(e -> {
            addMediaHome(this.homeItems);
        });
        
        cartImage.setOnMouseClicked(e -> {
            CartScreenHandler cartScreen;
            try {
                LOGGER.info("Clicked to view cart");
cartScreen = new CartScreenHandler(this.stage, Configs.CART_SCREEN_PATH);
                cartScreen.setHomeScreenHandler(this);
                cartScreen.setScreenTitle("Cart Screen");
                cartScreen.setBController(new ViewCartController());
                cartScreen.requestToViewCart(this);
                cartScreen.show();
            } catch (IOException e1) {
                throw new ViewCartException(Arrays.toString(e1.getStackTrace()).replaceAll(", ", "\n"));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        
        adminImage.setOnMouseClicked(e -> {
        	AdminScreenHandler adminScreen;
        	try {
        		LOGGER.info("Clicked to view admin screen");
        		adminScreen = new AdminScreenHandler(this.stage, Configs.ADMIN_SCREEN_PATH);
        		adminScreen.setHomeScreenHandler(this);
        		adminScreen.setScreenTitle("Admin Screen");
//        		adminScreen.setBController(new AdminController());
        		adminScreen.requestToViewAdminScreen(this);
        	
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}
        });
        
        addMediaHome(this.homeItems);
        addMenuItem(0, "Book", splitMenuBtnSearch);
        addMenuItem(1, "DVD", splitMenuBtnSearch);
        addMenuItem(2, "CD", splitMenuBtnSearch);
    }

    public void setImage(){
        // fix image path caused by fxml
        File file1 = new File(Configs.IMAGE_PATH + "/" + "Logo.png");
        Image img1 = new Image(file1.toURI().toString());
        aimsImage.setImage(img1);

        File file2 = new File(Configs.IMAGE_PATH + "/" + "cart.png");
        Image img2 = new Image(file2.toURI().toString());
        cartImage.setImage(img2);
    }

    public void addMediaHome(List items){
        ArrayList mediaItems = (ArrayList)((ArrayList) items).clone();
        hboxMedia.getChildren().forEach(node -> {
            VBox vBox = (VBox) node;
            vBox.getChildren().clear();
        });
        priceItems = new ArrayList<>();
        filteredItemsSort = items;
        while(!mediaItems.isEmpty()){
            hboxMedia.getChildren().forEach(node -> {
                int vid = hboxMedia.getChildren().indexOf(node);
                VBox vBox = (VBox) node;
                while(vBox.getChildren().size()<3 && !mediaItems.isEmpty()){
                    MediaHandler media = (MediaHandler) mediaItems.get(0);
                    priceItems.add(media.getMedia().getPrice());
                    vBox.getChildren().add(media.getContent());
                    mediaItems.remove(media);
                }
            });
            return;
        }
    }

    private void addMenuItem(int position, String text, MenuButton menuButton){
        MenuItem menuItem = new MenuItem();
        Label label = new Label();
        label.prefWidthProperty().bind(menuButton.widthProperty().subtract(31));
        label.setText(text);
label.setTextAlignment(TextAlignment.RIGHT);
        menuItem.setGraphic(label);
        menuItem.setOnAction(e -> {
            // empty home media
            hboxMedia.getChildren().forEach(node -> {
                VBox vBox = (VBox) node;
                vBox.getChildren().clear();
            });


            List filteredItems = new ArrayList<>();
            homeItems.forEach(me -> {
                MediaHandler media = (MediaHandler) me;
                if (media.getMedia().getTitle().toLowerCase().startsWith(text.toLowerCase())){
                    filteredItems.add(media);
                }
            });


            addMediaHome(filteredItems);
        });
        menuButton.getItems().add(position, menuItem);
    }

    @FXML
    private void handleTextFieldAction() {
        String enteredText = myTextField.getText();
        // empty home media
        hboxMedia.getChildren().forEach(node -> {
            VBox vBox = (VBox) node;
            vBox.getChildren().clear();
        });
        // filter only media with the choosen category
        List filteredItems = new ArrayList<>();
        homeItems.forEach(me -> {
            MediaHandler media = (MediaHandler) me;
            if(media.getMedia().getTitle().toLowerCase().contains(enteredText.toLowerCase())) {
                filteredItems.add(media);;
            }
        });

        // fill out the home with filted media as category
        addMediaHome(filteredItems);
    }

    @FXML
    private void handleSortAction() {
        hboxMedia.getChildren().forEach(node -> {
            VBox vBox = (VBox) node;
            vBox.getChildren().clear();
        });

        Collections.sort(priceItems);
        List<MediaHandler> filteredItemsSortAfter = new ArrayList<>();
        int j=0;
        for (int i : priceItems) {
            if (j == i) continue;
            j=i;
            filteredItemsSort.forEach(me -> {
                MediaHandler media = me;
                if (media.getMedia().getPrice() == i) {
                    filteredItemsSortAfter.add(media);
                }
            });
        }
        // fill out the home with filted media as category
        addMediaHome(filteredItemsSortAfter);
    }
}