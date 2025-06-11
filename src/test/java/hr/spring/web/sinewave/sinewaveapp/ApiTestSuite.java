package hr.spring.web.sinewave.sinewaveapp;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;


@Suite
@SelectClasses({
        AuthControllerTest.class,
        MobileAuthControllerTest.class,
        AdminControllerTest.class,
        PlaylistControllerTest.class,
        SongControllerTest.class,
        UserControllerTest.class,
        UserFriendControllerTest.class
})
public class ApiTestSuite {

}