package in.example.util;

import in.rentifi.app.BuildConfig;
import in.example.item.ItemProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public static String SERVER_URL = BuildConfig.server_url;

    public static final String IMAGE_PATH = SERVER_URL + "images/";

    public static final String LATEST_URL = SERVER_URL + "api.php?latest";

    public static final String ALL_PROPERTIES_URL = SERVER_URL + "api.php?all_properties";

    public static final String PROPERTIES_TYPE = SERVER_URL + "api.php?type_list";

    public static final String REGISTER_URL = SERVER_URL + "user_register_api.php?name=";

    public static final String LOGIN_URL = SERVER_URL + "user_login_api.php?email=";

    public static final String FORGOT_PASSWORD_URL = SERVER_URL + "user_forgot_pass_api.php?email=";

    public static final String ABOUT_URL = SERVER_URL + "api.php?app_details";

    public static final String SEARCH_URL = SERVER_URL + "api.php?search_text=";

    public static final String USER_PROFILE_URL = SERVER_URL + "user_profile_api.php?id=";

    public static final String USER_PROFILE_UPDATE_URL = SERVER_URL + "user_profile_update_api.php";

    public static final String SINGLE_PROPERTY_URL = SERVER_URL + "api.php?property_id=";

    public static final String HOME_URL = SERVER_URL + "api.php?home";

    public static final String RATING_URL = SERVER_URL + "api_rating.php?property_id=";

    public static final String MOST_POPULAR_URL = SERVER_URL + "api.php?popular_properties";

    public static final String CONTACT_US_URL = SERVER_URL + "api_contact.php?name=";

    public static final String UPLOAD_PROPERTIES_URL = SERVER_URL + "user_property_add_api.php";

    public static final String MY_PROPERTIES_URL = SERVER_URL + "user_property_list_api.php?user_id=";

    public static final String ADVANCE_SEARCH_URL = SERVER_URL + "api.php?all_properties&verified=";

    public static final String DISTANCE_URL = SERVER_URL + "api.php?properties_distance&user_lat=";

    public static final String PRICE_URL = SERVER_URL + "api.php?properties_min_max_price=";

    public static final String ARRAY_NAME = "REAL_ESTATE_APP";


    public static final String PROPERTY_ID = "p_id";
    public static final String PROPERTY_TITLE = "property_name";
    public static final String PROPERTY_DESC = "property_description";
    public static final String PROPERTY_PHONE = "property_phone";
    public static final String PROPERTY_ADDRESS = "property_address";
    public static final String PROPERTY_LATITUDE = "property_map_latitude";
    public static final String PROPERTY_LONGITUDE = "property_map_longitude";
    public static final String PROPERTY_IMAGE = "property_thumbnail_b";
    public static final String PROPERTY_BED = "property_bed";
    public static final String PROPERTY_BATH = "property_bath";
    public static final String PROPERTY_AREA = "property_area";
    public static final String PROPERTY_AMENITIES = "property_amenities";
    public static final String PROPERTY_PRICE = "property_price";
    public static final String PROPERTY_RATE = "rate_avg";
    public static final String PROPERTY_PURPOSE = "property_purpose";
    public static final String PROPERTY_FLOOR_PLAN = "property_floor_plan";
    public static final String PROPERTY_TOTAL_RATE= "total_rate";
    public static final String PROPERTY_FUR= "furnishing";
    public static final String PROPERTY_SUITABLE_FOR= "suitable_for";
    public static final String PROPERTY_VERY= "verified";

    public static final String TYPE_ID = "tid";
    public static final String TYPE_NAME = "type_name";

    public static final String APP_NAME = "app_name";
    public static final String APP_IMAGE = "app_logo";
    public static final String APP_VERSION = "app_version";
    public static final String APP_AUTHOR = "app_author";
    public static final String APP_CONTACT = "app_contact";
    public static final String APP_EMAIL = "app_email";
    public static final String APP_WEBSITE = "app_website";
    public static final String APP_DESC = "app_description";
    public static final String APP_PRIVACY_POLICY = "app_privacy_policy";

    public static final String GALLERY_ARRAY_NAME = "galley_image";
    public static final String GALLERY_IMAGE_NAME = "image_name";

    public static final String USER_NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE = "phone";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_ADDRESS = "address";

    public static final String HOME_LATEST_ARRAY = "latest_property";
    public static final String HOME_FEATURED_ARRAY = "featured_property";
    public static final String HOME_POPULAR_ARRAY="popular_property";

    public static int GET_SUCCESS_MSG;
    public static final String MSG = "msg";
    public static final String SUCCESS = "success";
    public static final String ADS_BANNER_ID="banner_ad_id";
    public static final String ADS_FULL_ID="interstital_ad_id";
    public static final String ADS_BANNER_ON_OFF="banner_ad";
    public static final String ADS_FULL_ON_OFF="interstital_ad";
    public static final String ADS_PUB_ID="publisher_id";
    public static final String ADS_CLICK="interstital_ad_click";
    public static String SAVE_ADS_BANNER_ID,SAVE_ADS_FULL_ID,SAVE_ADS_BANNER_ON_OFF,SAVE_ADS_FULL_ON_OFF,SAVE_ADS_PUB_ID,SAVE_ADS_CLICK;
    public static String SEARCH_FIL_ID="";
    public static ArrayList<ItemProperty> mList=new ArrayList<>();
    public static String USER_LATITUDE ;
    public static String USER_LONGITUDE;

}
