package me.wcc.ui;

import java.net.URL;
import java.util.*;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import me.wcc.http.HttpClient;
import me.wcc.http.auth.AuthParams;
import me.wcc.http.auth.OAuth2Authenticator;
import me.wcc.http.entity.ResponseEntity;
import me.wcc.util.RefreshProperties;
import org.apache.commons.lang3.StringUtils;

/**
 * @author TTcheng
 */
@Slf4j
@SuppressWarnings("unused")
public class Controller implements Initializable {

    private HttpClient httpClient;
    private String username;
    private String password;
    private String authUrl;

    @FXML
    private HBox urlBox;
    @FXML
    public HBox serviceBox;
    @FXML
    public HBox userInfoBox;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private VBox rootBox;

    @FXML
    private JFXTextArea showArea;
    @FXML
    private JFXTextField urlField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXTextField serviceNameField;
    @FXML
    private JFXTextField serviceVersionField;

    @FXML
    private JFXButton refreshBtn;
    @FXML
    public JFXButton saveBtn;


    @FXML
    public void onSave(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source instanceof JFXButton) {
            JFXButton btn = (JFXButton) source;
            if (!"saveBtn".equals(btn.getId())) {
                return;
            }
            if (checkAndUpdate()) {
                RefreshProperties.update(authUrl, username, password);
            }
        }
    }

    @FXML
    private void onRefreshAll(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof JFXButton) {
            JFXButton btn = (JFXButton) source;
            if (!"refreshBtn".equals(btn.getId())) {
                return;
            }
            boolean update = checkAndUpdate();
            if (StringUtils.isAnyBlank(authUrl, username, password)){
                return;
            }
            if (!update && null != httpClient){
                doRefreshAll();
                return;
            }
            AuthParams authParams = new AuthParams(username, password, "client", "secret");
            OAuth2Authenticator authenticator = new OAuth2Authenticator(authUrl, authParams);
            httpClient = new HttpClient(parseDomain(authUrl), authenticator);
            doRefreshAll();
        }
    }

    /**
     * 检查内容是否为空，并判断是否有更新
     *
     * @return 是否更新
     */
    private boolean checkAndUpdate() {
        String inputUrl = this.urlField.getText();
        if (inputUrl == null || "".equals(inputUrl.trim())) {
            alert("您必须输入一个合法的URL");
            return false;
        }
        String inputUsername = usernameField.getText();
        String inputPassword = passwordField.getText();
        if (StringUtils.isBlank(inputUsername) || StringUtils.isBlank(inputPassword)) {
            alert("用户名和密码不能为空！");
            return false;
        }
        if (Objects.equals(authUrl, inputUrl) && Objects.equals(username, inputUsername)
                && Objects.equals(password, inputPassword)) {
            // 没有改变
            return false;
        }
        this.authUrl = inputUrl;
        this.password = inputPassword;
        this.username = inputUsername;
        return true;
    }

    private String parseDomain(String url) {
        if (url.startsWith("http")) {
            String[] split = url.split("/");
            if (split.length > 3) {
                return split[0] + "//" + split[2];
            }
        }
        return url.substring(0, authUrl.indexOf('/'));
    }

    private static final String SWAGGER_REFRESH = "/swagger/docs/swagger/refresh/%s?version=%s";
    private static final String ROUTE_REFRESH = "/hcnf/v1/monitor/refresh-route";
    private static final String PERMISSION_REFRESH = "/iam/v1/permission/fresh/%s?metaVersion=%s";

    private void doRefreshAll() {
        String serviceName = serviceNameField.getText();
        String version = serviceVersionField.getText();
        if (StringUtils.isBlank(serviceName) || StringUtils.isBlank(version)) {
            alert("请输入服务名称和版本！");
            return;
        }
        showArea.clear();
        ResponseEntity swagger = httpClient.post(String.format(SWAGGER_REFRESH, serviceName, version));
        appendShowInfo(swagger.toString());
        appendShowInfo("=====================================");
        ResponseEntity route = httpClient.post(ROUTE_REFRESH);
        appendShowInfo(route.toString());
        appendShowInfo("=====================================");
        ResponseEntity permission = httpClient.post(String.format(PERMISSION_REFRESH, serviceName, version));
        appendShowInfo(permission.toString());
    }

    private void alert(String msg) {
        JFXButton btn = new JFXButton("Okay!");
        AlertMaker.showMaterialDialog(rootPane, anchorPane, Collections.singletonList(btn), msg, null);
    }

    private void setShowInfo(String info) {
        showArea.setText(info);
    }

    private void appendShowInfo(String info) {
        showArea.appendText(info + "\n");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showArea.setEditable(false);
        Map<String, String> properties = RefreshProperties.read();
        String tokenUrl = properties.get(RefreshProperties.KEY_TOKEN_URL);
        String storedUsername = properties.get(RefreshProperties.KEY_USERNAME);
        String storedPassword = properties.get(RefreshProperties.KEY_PASSWORD);

        if (StringUtils.isNotBlank(tokenUrl)) {
            authUrl = tokenUrl;
            urlField.setText(tokenUrl);
        }
        if (StringUtils.isNotBlank(storedPassword)) {
            password = storedPassword;
            passwordField.setText(storedPassword);
        }
        if (StringUtils.isNotBlank(storedUsername)) {
            username = storedUsername;
            usernameField.setText(storedUsername);
        }
    }
}
