package com.msc.ar_drawing.utils.i_ringtone;


import com.msc.ar_drawing.R;

public class CONST {

    public static final String FAIL_TASK = "FAIL";
    public static final int REQUEST_FOR_PICK_CONTACT = 505;
    public static final int REQUEST_FOR_WRITE_SETTINGS = 404;
    public static final String SUCCESS_TASK = "SUCCESS";
    public static String PACKAGE_NAME = "APP";
    public static String RATE = "RATE";
    public static String SELECTED_RAW_FILE_NAME = null;
    public static String SELECTED_SHOW_FILE_NAME = null;
    public static int SELECTED_TYPE = 0;
    public static String SELECTED_URL = null;
    public static String SHARE_AUDIO_TITLE = "Share Audio File";
    public static int userCount = 1;
    public static int POS = 0;

    public static int[] rawArray = new int[]{
            R.raw.animals_alpaka,
            R.raw.animals_antylopa
    };

    public static String[] rawName = new String[]{
            "animals_alpaka", "animals_antylopa"
    };

//    public static ArrayList<JcAudio> getRingtoneJC(Context context) {
//
//        ArrayList<JcAudio> jcAudios = new ArrayList<>();
//
//
//        Field[] fields = R.raw.class.getFields();
//
//
//        for (Field value : fields) {
//            String resId = value.getName();
//            try {
//                Class res = R.raw.class;
//                Field field = res.getField(resId);
//                int rawId = field.getInt(null);
//
//                jcAudios.add(JcAudio.createFromRaw(value.getName(), rawId));
//            } catch (Exception e) {
//                Log.e("MyTag", "Failure to get drawable id.", e);
//            }
//
//
//        }
//
//
//  /*      for (int i = 0; i < rawarray.length; i++) {
//
//            jcAudios.add(JcAudio.createFromRaw(songnamearray[i],rawarray[i]));
//
//        }*/
//
//
//        return jcAudios;
//    }
//
//
//    public static List<GSRingtone> getRingtone(Context context) {
//
//        List<GSRingtone> arrayList = new ArrayList();
//
//        Field[] fields = R.raw.class.getFields();
//
//        for (int i = 0; i < fields.length; i++) {
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(fields[i].getName());
//            stringBuilder.append(".mp3");
//            arrayList.add(new GSRingtone(stringBuilder.toString(), fields[i].getName(), rawName[i]));
//        }
//
//        /*for (int i = 0; i < fields.length; i++) {
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(fields[i].getName());
//            stringBuilder.append(".mp3");
//            arrayList.add(new GSRingtone(stringBuilder.toString(), fields[i].getName(), songnamearray[i]));
//        }*/
//
//
//        return arrayList;
//    }

}
