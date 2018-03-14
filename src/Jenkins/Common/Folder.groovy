package Jenkins.Common

class Folder extends File {

    /**
     * Конструктор объекта Folder
     * @param s Имя файла
     */
    Folder(String s) {
        super(s)
    }

    /**
     * Удаление каталога (себя).
     * Метод просто переопубликован специально, чтобы Jenkins не требовал дополнительных разрешений для удаления каталога.
     * @return Получилось ли удалить.
     */
    @Override
    boolean delete() {
        super.delete()
    }

    /**
     * Преобразование маски поиска файлов в регулярное выражение.
     * @param pattern Маска файлов.
     * @return Регулярное выражение, соответствующее маске файлов.
     */
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


    /**
     * Определение признака, что внутри есть файлы.
     * @param mask Опциональная маска имени файлов.
     * @param minModifyDate Опциональная минимальная дата/время изменения файла.
     * @param maxModifyDate Опциональная максимальная дата/время изменения файла.
     * @return Признак, что внутри есть файлы
     */
    boolean hasFiles(String mask = null, Date minModifyDate = null, Date maxModifyDate = null){
        File[] files = findFiles(mask, minModifyDate, maxModifyDate)
        files.length>0
    }

    /**
     * Поиск файлов внутри себя. Нерекурсивно.
     * @param mask Опциональная маска имени файлов.
     * @param minModifyDate Опциональная минимальная дата/время изменения файла.
     * @param maxModifyDate Опциональная максимальная дата/время изменения файла.
     * @return Массив найденных файлов (объектов типа File)
     */
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

    /**
     * Поиск каталогов внутри себя. Нерекурсивно.
     * @param mask Опциональная маска имени каталогов.
     * @return Массив каталогов (объектов типа File)
     */
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

    /**
     * Проверка, что файл заблокирован.
     * Работа не проверялась. И скорее всего, будет некорректно работать в каталогах без разрешения на изменение.
     * @param file Проверяемый файл
     * @return Результат проверки. Если Истина, значит файл заблокирован.
     */
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
