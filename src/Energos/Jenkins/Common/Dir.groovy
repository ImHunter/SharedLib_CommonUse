package Energos.Jenkins.Common

import java.io.File

class Dir extends File {

    Dir(String s) {
        super(s)
    }

    boolean hasFiles(String mask = null, Date minModifyDate = null){
        File[] files = findFiles(mask, minModifyDate)
        files.length>0
    }

    File[] findFiles(String mask = null, Date minModifyDate = null){
        ArrayList<File> lst = new ArrayList()
        eachFileMatch(mask==null ? '*.*' : mask) {File f ->
            if (f.isFile()) {
                if (minModifyDate!=null && (new Date(f.lastModified())>=minModifyDate)) {
                    lst.add(f)
                }
            }
        }
        lst.toArray()
    }

}
