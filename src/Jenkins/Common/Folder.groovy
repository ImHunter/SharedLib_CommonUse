package Jenkins.Common

import org.apache.ivy.plugins.repository.ssh.Scp

import java.security.MessageDigest

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

    /**
     * Удаление устаревших файлов с выполнением проверки уникальности файлов по контрольной сумме.
     * @param fileMask Маска, по которой будут искаться удаляемые файлы
     * @param uniqueMaxCount Максимальное количество уникальных файлов, которые нужно оставить.
     */
    void leaveLastUniqueFiles(def fileMask = '*.*', def uniqueMaxCount = 5){

        // чтобы uniqueMaxCount можно было передать пустым или строкой
        def uniqueCount
        if (uniqueMaxCount==null)
            uniqueCount = 0
        else
            uniqueCount = Integer.decode(uniqueMaxCount.toString())

        // ищем все файлы
        def files = findFiles(fileMask)

        if (uniqueCount!=null && uniqueCount>0 && files.size()>0) {
            // заполняем информацию о файлах
            def infos = []
            files.each {
                File f = it
                FileInfo fi = new FileInfo()
                fi.fileName = it.toString()
                fi.checkSum = generateMD5(f)
                fi.lastModifyDT = f.lastModified()
                infos.add(fi)
            }
            // сортируем по дате изменения в порядке убывания
            infos.sort{FileInfo x, FileInfo y -> x.lastModifyDT <=> y.lastModifyDT}
            infos = infos.reverse()
            // помечаем файлы на удаление
            def curIndex
            def sumsCount = 0
            infos.each {
                if (!it.toDelete) {
                    if (sumsCount<uniqueCount) {
                        def curSum = it.checkSum
                        def filesBySum = infos.findAll { FileInfo fileInfo -> fileInfo.checkSum == curSum }
                        filesBySum.sort { FileInfo x, FileInfo y -> x.lastModifyDT <=> y.lastModifyDT }
                        filesBySum = filesBySum.reverse()
                        curIndex = 0
                        filesBySum.each { FileInfo fileInfo ->
                            fileInfo.toDelete = curIndex != 0
                            curIndex++
                        }
                        sumsCount++
                    } else {
                        it.toDelete = true
                    }
                }
            }
            // удаляем помеченные
            infos.each {
                if (it.toDelete) {
                    def fileDel = new File(it.fileName)
                    fileDel.delete()
                }
            }

        }
    }

    /**
     * Функция для подсчета контрольной суммы файла по алгоритму MD5
     * @param file Файл, по которому считается контрольная сумма
     * @return Значение контрольной суммы
     */
    @NonCPS
    private String generateMD5(final file) {
        MessageDigest digest = MessageDigest.getInstance("MD5")
        file.withInputStream(){ is ->
            byte[] buffer = new byte[8192]
            int read = 0
            while( (read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] md5sum = digest.digest()
        BigInteger bigInt = new BigInteger(1, md5sum)

        return bigInt.toString(16).padLeft(32, '0')
    }

    /**
     * Внутренний класс с информацией о файле. Используется в функции leaveLastUniqueFiles
     */
    class FileInfo{
        String fileName
        long lastModifyDT
        String checkSum
        Boolean toDelete = false
    }

    String getDatedFileName(String prefix = 'config_', String suffix = '.cf', String dateFormatString = 'yyyyMMdd_HHmm', def dateVal = null){
        Date dt = dateVal==null ? Date.newInstance() : dateVal
        prefix.concat(dt.format(dateFormatString)).concat(suffix)
    }
}
