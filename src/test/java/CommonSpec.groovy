import me.wcc.http.HttpClient
import me.wcc.http.auth.AuthParams
import me.wcc.http.auth.OAuth2Authenticator
import me.wcc.http.auth.Token
import me.wcc.http.auth.TokenHolder
import me.wcc.util.RefreshProperties
import spock.lang.Specification

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/**
 * @author chuncheng.wang@hand-china.com 2019/8/3 下午5:39
 */
class CommonSpec extends Specification {

    void "test datetime"(){
         DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        def dateTime = Instant.now().atZone(ZoneId.systemDefault())
        def timeString = dateTime.format(dateTimeFormatter)
        expect:
        println(timeString)
    }

    void "test Properties"(){
        when:
        RefreshProperties.update(Collections.singletonMap("username","jesse"))
        def properties = RefreshProperties.read()
        then:
        noExceptionThrown()
        properties.get("username") == "jesse"
        println(properties)
    }

    void "test any"(){
        def encode = Base64.getEncoder().encode("wcc19960314".getBytes())
        expect:
        println(new String(encode))
    }
    void "test toObject"() {
        given:
        def json = "{\"access_token\":\"3a23ed20-0771-4e89-a757-88ece4841eea\",\"token_type\":\"bearer\",\"refresh_token\":\"e7864d43-fd0e-4ae2-9be1-f3b9a1afaf7f\",\"expires_in\":85840,\"scope\":\"default\"}"
        when:
        def token = Token.fromJson(json)
        then:
        "3a23ed20-0771-4e89-a757-88ece4841eea" == token.value
        85840L == token.expiresIn
        noExceptionThrown()
    }

    void "test client"() {
        given:
        def domain = "http://dev.hdsp.hand.com:8080"
        def authParams = new AuthParams("83563164", "wcc19960314", "client", "secret")
        def authenticator = new OAuth2Authenticator(domain + "/oauth/oauth/token", authParams)
        HttpClient httpClient = new HttpClient(domain, authenticator)
        when:
        def response = httpClient.get("/xpbi/v1/4/reports?page=0&size=10")
        def token = TokenHolder.token(domain)
        then:
        // "d2NjMTk5NjAzMTQ=" == authParams.password
        noExceptionThrown()
        response.success
        println(token)
        println(response)
    }
}