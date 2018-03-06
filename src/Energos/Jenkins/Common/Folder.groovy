package Energos.Jenkins.Common

import java.io.File
import java.util.regex.Pattern

class Folder extends File {

    Folder(String s) {
        super(s)
    }

    @Override
    boolean delete() {
        return super.delete()
    }

    @NonCPS
    static String wildcardToRegexp(String pattern) {

        char[] ESCAPES = [ '$', '^', '[', ']', '(', ')', '{', '|', '+', '\\', '.', '<', '>' ]
        String result = "^";

        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            boolean escaped = false;
            for (int j = 0; j < ESCAPES.length; j++) {
                if (ch == ESCAPES[j]) {
                    result += "\\" + ch;
                    escaped = true;
                    break;
                }
            }

            if (!escaped) {
                if (ch == '*') {
                    result += ".*";
                } else if (ch == '?') {
                    result += ".";
                } else {
                    result += ch;
                }
            }
        }
        result += '$'
        return result;
    }


    boolean hasFiles(String mask = null, Date minModifyDate = null, Date maxModifyDate = null){
        File[] files = findFiles(mask, minModifyDate, maxModifyDate)
        files.length>0
    }

    @NonCPS
    def findFiles(String mask = null, Date minModifyDate = null, Date maxModifyDate = null){

        List<File> lst = new ArrayList<File>()
        boolean dateChecked, matched
        String patt = wildcardToRegexp(mask==null ? '*.*' : mask)

        eachFile { File f ->
            matched = f.isFile() && f.getName().matches(patt)
            if (matched) {
                dateChecked = minModifyDate==null || (minModifyDate!=null && (new Date(f.lastModified())>=minModifyDate))
                if (dateChecked)
                    dateChecked = maxModifyDate==null || (maxModifyDate!=null && (new Date(f.lastModified())<maxModifyDate))
                if (dateChecked)
                    lst.add(f)
            }
        }
        lst.toArray()
    }

    @NonCPS
    def findDirs(String mask = null){

        List<File> lst = new ArrayList<File>()
        boolean matched
        String patt = wildcardToRegexp(mask==null ? '*.*' : mask)

        eachDir { dir ->
            matched = dir.getName().matches(patt)
            if (matched) {
                lst.add(dir)
            }
        }
        lst.toArray()
    }

    /**
     * Проверяет валидность созданного объекта Folder.
     * @return Возвращает Истина, если Folder - это существующий каталог.
     */
    boolean enabled() {
        exists() && isDirectory()
    }



    static boolean isFilelocked(File file) {
        boolean retVal = false
        try {
            FileInputStream inp = new FileInputStream(file);
            inp.close()
        } catch (FileNotFoundException e) {
            if(file.exists()){
                retVal = true
            }
        } catch (Exception e) {
            e.printStackTrace()
            retVal = true
        }

        retVal
    }


}
