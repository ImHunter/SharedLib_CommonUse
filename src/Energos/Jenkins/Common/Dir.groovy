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
        boolean dateChecked
        eachFileMatch(mask==null ? '*.*' : mask) {File f ->
            if (f.isFile()) {
                dateChecked = minModifyDate==null || (minModifyDate!=null && (new Date(f.lastModified())>=minModifyDate))
                if (dateChecked)
                    lst.add(f)
            }
        }
        lst.toArray()
    }

}
