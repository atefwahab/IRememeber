package wmad.iti.constants;

/**
 * Created by atef on 5/18/2016.
 */
public abstract class Urls {
    public final static String WEB_SERVICE_URL = "";
    //public final static String IP_ADDRESS = "192.168.1.3";
    public final static String IP_ADDRESS = "192.168.1.16";
    public final static String PORT_NUMBER = "8084";
    public final static String WEB_SERVICE_lOGIN_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/login/";
    public final static String WEB_SERVICE_REGISTER_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/regist/register";
    public final static String WEB_SERVICE_GET_REQUEST_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/request/getRequests";
    public final static String WEB_SERVICE_RESPOND_TO_REQUEST_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/request/respondToRequest";
    public final static String WEB_SERVICE_SEARCH_PATIENT_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/patient";
    public final static String WEB_SERVICE_SEND_REQUEST_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/request/addRequest";
    public final static String WEB_SERVICE_GET_PATIENT_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/relation/getPatients";
    public final static String WEB_SERVICE_REMOVE_PATIENT_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/relation/removePatient";
    public final static String WEB_SERVICE_GET_RELATIVES_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/relation/getRelatives";

    public final static String IMAGE_UPLOAD_URL="http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/regist/file";


    public final static String WEB_SERVICE_FULL_NAME_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/update/full";
    public final static String WEB_SERVICE_PHONE_NUMBER_URL="http://"+ Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/update/phone";
    public final static String WEB_SERVICE_COUNTRY_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/update/country";

    public final static String WEB_SERVICE_CITY_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/update/city";
    public final static String WEB_SERVICE_ADDRESS_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/update/address";

    public final static String SAVE_MEMORY_TEXT_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/memory/saveMemoryText";
    public final static String GET_MEMORIES_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/memory/getMemories";
    public final static String EDIT_MEMORY_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/memory/updateTextMemory";
    public final static String DELETE_MEMORY_URL = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/memory/deleteMemory";
    public final static String MEMORY_IMAGE_UPLOAD_URL="http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/memoryPhotoesWS/image";
    public final static String IMAGE_MEMORY_NULL="http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/images/null";

    public final static String WEB_SERVICE_UPDATE_TOKEN = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/locationManager/registerToken";
    public final static String WEB_SERVICE_REQUEST_UPDATE_LOCATION = "http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/service/locationManager/requestLocation";
}
