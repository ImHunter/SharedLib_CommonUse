package Energos.Jenkins.Common

import java.io.File
import java.util.regex.Pattern

class Dir extends File {

    Dir(String s) {
        super(s)
    }

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


    boolean hasFiles(String mask = null, Date minModifyDate = null){
        File[] files = findFiles(mask, minModifyDate)
        files.length>0
    }

    def findFiles(String mask = null, Date minModifyDate = null){

        ArrayList lst = new ArrayList()
        boolean dateChecked, matched
        String patt = wildcardToRegexp(mask==null ? '*.*' : mask)

        eachFile { def f ->
            if (!lst.add(f))
                new Exception('хз чего так')
//            matched = true || (f.getName().matches(patt) && f.isFile())
//            if (matched) {
//                dateChecked = true || minModifyDate==null || (minModifyDate!=null && (new Date(f.lastModified())>=minModifyDate))
//                if (dateChecked)
//                    lst.add(f)
//            }
        }
        lst
    }

    File[] findDirs(String mask = null){

        ArrayList<File> lst = new ArrayList()
        boolean matched
        String patt = wildcardToRegexp(mask==null ? '*.*' : mask)

        eachDir { dir ->
            matched = dir.getName().matches(patt)
            if (matched) {
                lst.add(dir)
            }
        }
        lst.toArray() as File[]
    }


}
