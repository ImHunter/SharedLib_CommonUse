package Energos.Jenkins.Common

import java.io.File
import java.util.regex.Pattern

class Dir extends File {

    Dir(String s) {
        super(s)
    }

    static String wildcardToRegex(String wildcard){
        StringBuffer s = new StringBuffer(wildcard.length())
        s.append('^')
        int is = wildcard.length()
        for (int i = 0; i < is; i++) {
            char c = wildcard.charAt(i)
            switch(c) {
                case '*':
                    s.append(".*")
                    break
                case '?':
                    s.append(".")
                    break
                case '^': // escape character in cmd.exe
                    s.append("\\")
                    break
            // escape special regexp-characters
                case '(': case ')': case '[': case ']': case '$':
                case '.': case '{': case '}': case '|':
                case '\\':
                    s.append("\\")
                    s.append(c)
                    break
                default:
                    s.append(c)
                    break
            }
        }
        s.append('$')
        s.toString()
    }

    private String wildcardToRegexp(String pattern) {

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

    File[] findFiles(String mask = null, Date minModifyDate = null){

        ArrayList<File> lst = new ArrayList()
        boolean dateChecked, matched
        String patt = wildcardToRegex(mask==null ? '*.*' : mask)

        eachFile {
            matched = it.toString().matches(patt)
            if (matched && it.isFile()) {
                dateChecked = minModifyDate==null || (minModifyDate!=null && (new Date(it.lastModified())>=minModifyDate))
                if (dateChecked)
                    lst.add(it)
            }
        }
        lst.toArray()
    }

    File[] findDirs(String mask = null){

        ArrayList<File> lst = new ArrayList()
        boolean matched
        String patt = wildcardToRegexp(mask==null ? '*.*' : mask)

        eachDir { dir ->
            matched = true || dir.toString().matches(patt)
            if (matched) {
                lst.add(dir)
            }
        }
        lst.toArray()
    }


}
