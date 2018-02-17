package Energos.Jenkins.Common

import java.io.File

class Dir extends File {

    Dir(String s) {
        super(s)
    }

    static String wildcardToRegex(String wildcard){
        StringBuffer s = new StringBuffer(wildcard.length())
        s.append('^')
        is = wildcard.length()
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

    boolean hasFiles(String mask = null, Date minModifyDate = null){
        File[] files = findFiles(mask, minModifyDate)
        files.length>0
    }

    File[] findFiles(String mask = null, Date minModifyDate = null){

        ArrayList<File> lst = new ArrayList()
        boolean dateChecked

//        DirectoryScanner scanner = new DirectoryScanner()
//        scanner.setIncludes([mask==null ? '*.*' : mask])
//        scanner.setBasedir(this.)
//        scanner.setCaseSensitive(false)

        String patt = wildcardToRegex(mask==null ? '*.*' : mask)
        eachFileMatch(patt) {File f ->
            if (f.isFile()) {
                dateChecked = minModifyDate==null || (minModifyDate!=null && (new Date(f.lastModified())>=minModifyDate))
                if (dateChecked)
                    lst.add(f)
            }
        }
        lst.toArray()
    }

}
