package Jenkins.Common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.ResolverStyle

/**
 * Класс-утилита для манипуляций с датами.
 * Создан, чтобы инкапсулировать специфические для CI (в частности, для Ванессы или Деплойки) операции. И чтобы при этом не выходить из песочницы Jenkins.
 */
@NonCPS
class DateNow extends Date{

    /**
     * Создаем объект типа Calendar на основании текущего значения объекта
     * @return Новый объект Calendar
     */
    Calendar toCalendar(){
        Calendar retVal = Calendar.getInstance()
        retVal.setTime(this)
        retVal
    }

    /**
     * Конвертация строки в Date
     * @param strVal Строковое представление
     * @param fmt Форматная строка, согласно которой будет интерпретироваться strVal
     * @return Дата из строки
     */
    private static Date getFromString(String strVal, String fmt) {

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseStrict()
                .appendPattern(fmt)
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT)
        def ldt
        def retVal
        if (strVal.length()==8) {
            ldt = LocalDate.parse(strVal, formatter)
            retVal = Date.from(ldt.atStartOfDay(ZoneId.systemDefault()).toInstant())
        } else {
            ldt = LocalDateTime.parse(strVal, formatter)
            ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault())
            GregorianCalendar cal = GregorianCalendar.from(zdt)
            retVal = cal.getTime()
        }
        retVal
    }

    /**
     * Устанавливает значение текущего объекта из значения типа Date, Calendar или String
     * @param value Устанавливаемое значение.
     * При передаче параметра строкой последовательно делаются попытки преобразования с использованием форматов ггггММддччммсс ('uuuuMMddHHmmss'), ггггММдд ('uuuuMMdd'), гггг-ММ-ддTчч:мм:сс ("uuuu-MM-dd'T'HH:mm:ss")
     */
    def setFromValue(def value) {
        if (value instanceof Date)
            setTime((value as Date).getTime())
        else if (value instanceof Calendar)
            setTime((value as Calendar).getTime().getTime())
        else if (value instanceof String || value instanceof  GString) {
            String strVal = value.toString()
            try {
                setFromValue(getFromString(strVal, 'uuuuMMddHHmmss'))
            } catch (e) {
                try {
                    setFromValue(getFromString(strVal, 'uuuuMMdd'))
                } catch (ee) {
                    setFromValue(getFromString(strVal, "uuuu-MM-dd'T'HH:mm:ss".toString()))
                }
            }
        } else
            new Exception("Непредусмотренный тип значения $value")
        this
    }

    /**
     * Установка значения какой-либо составляющей даты
     * @param part Устанавливаемая часть - какая-либо константа Calendar. Например, Calendar.HOUR_OF_DAY
     * @param value Значение устанавливаемой части
     * @return Возвращается текущий объект с установленным значением части даты
     */
    private def setTimePart(int part, int value) {
        def c = toCalendar()
        c.set(part, value)
        this.setTime(c.getTime().getTime())
        this
    }

    /**
     * Установка времени путем передачи значений частей времени
     * @param hr Устанавливаемые часы. При передаче значения null, уже установленное значение часов меняться не будет.
     * @param min Устанавливаемые минуты. При передаче значения null, уже установленное значение минут меняться не будет.
     * @param sec Устанавливаемые секунды. При передаче значения null, уже установленное значение секунд меняться не будет.
     * @return Текущий объект с измененным временем.
     */
    def setTimeParts(Integer hr = null, Integer min = null, Integer sec = null){
        if (hr!=null)
            setTimePart(Calendar.HOUR_OF_DAY, hr)
        if (min!=null)
            setTimePart(Calendar.MINUTE, min)
        if (sec!=null)
            setTimePart(Calendar.SECOND, sec)
        this
    }

    /**
     * Установка даты путем передачи значений частей даты.
     * Если какую-то из частей даты устанавливать не нужно (требуется сохранить ее первоначальное значение), то нужно передать значение null.
     * @param yy Устанавливаемое значение года.
     * @param mm Устанавливаемое значение месяца.
     * @param dd Устанавливаемое значение дня.
     * @return Текущий объект с измененной датой.
     */
    def setDateParts(Integer yy = null, Integer mm = null, Integer dd = null){
        if (yy!=null)
            setTimePart(Calendar.YEAR, yy)
        if (mm!=null)
            setTimePart(Calendar.MONTH, mm - 1)
        if (dd!=null)
            setTimePart(Calendar.DAY_OF_MONTH, dd)
        this
    }

    /**
     * Форматирование даты в представление 1С.
     * Если в дате присуствует время, то форматируется в строку yyyyMMddHHmmss. Иначе форматируется в yyyyMMdd.
     * @return Строковое представление даты
     */
    String formatFor1C(){
        String fmtStr = hasTime() ? 'yyyyMMddHHmmss' : 'yyyyMMdd'
        this.format(fmtStr)
    }

    /**
     * Форматирование даты в представление для XML (yyyy-MM-dd'T'HH:mm:ss).
     * @return Строковое представление даты
     */
    String formatForXML(){
        String fmtStr = "yyyy-MM-dd'T'HH:mm:ss"
        this.format(fmtStr)
    }

    private static void parseRangeWith(String range, Closure body){
        boolean hasColon = range.contains(':')
        String[] parts
        if (hasColon)
            parts = range.split(':')
        else {
            ArrayList<String> lst = new ArrayList()
            String inStr = range
            int len
            while (!inStr.isEmpty()) {
                len = inStr.length()
                if (len>=2)
                    len = 2
                lst.add(inStr.substring(0, len))
                inStr = inStr.substring(len)
            }
            parts = lst.toArray()
        }
        def hh = null, mm, ss
        if (parts.length>0)
            hh = Integer.valueOf(parts[0]).intValue()
        if (parts.length>1)
            mm = Integer.valueOf(parts[1]).intValue()
        else
            mm = 0
        if (parts.length>2)
            ss = Integer.valueOf(parts[2]).intValue()
        else
            ss = 0
        body.call(hh, mm, ss)
    }

    boolean inRange(String safeRangeFrom, String safeRangeTo){
        def fromDt = this.clone()
        def toDt = this.clone()
        parseRangeWith(safeRangeFrom){ int hh, int mm, int ss ->
            fromDt.setTimeParts(hh, mm, ss)
        }
        parseRangeWith(safeRangeTo){ int hh, int mm, int ss ->
            toDt.setTimeParts(hh, mm, ss)
        }
        if (fromDt>this)
            fromDt = fromDt.previous()
        def retVal = this>=fromDt && this<=toDt

        retVal
    }

    void addMinutes(int minutes) {
        Calendar c = this.toCalendar()
        c.add(Calendar.MINUTE, minutes)
        setFromValue(c)
    }

    def getSafeDate(String safeRangeFrom, String safeRangeTo, Integer minutesDelta = null) {
        def retVal = this.clone()
        if (retVal.inRange(safeRangeFrom, safeRangeTo)) {
            parseRangeWith(safeRangeFrom) { hh, mm, ss ->
                retVal.setTimeParts(hh, mm, ss)
                if (retVal.compareTo(this)>0)
                    --retVal
            }
        } else {
            retVal.addMinutes(minutesDelta)
        }
        retVal
    }

    /**
     * Разбивает значение объекта (дату и время) на составляющие.
     * Составляющие можно прочитать в замыкании (closure) либо, как результат выполнения функции.
     * @param closure Замыкание, куда передаются составляющие даты в порядке год, месяц, день, час, мин, сек
     * @return Массив составляющих в порядке год, месяц, день, час, мин, сек
     */
    def splitWith(Closure closure = null) {

        int yy, MM, dd, hh, mm, ss
        Calendar c = toCalendar()

        yy = c.get(Calendar.YEAR)
        MM = c.get(Calendar.MONTH) + 1
        dd = c.get(Calendar.DAY_OF_MONTH)
        hh = c.get(Calendar.HOUR_OF_DAY)
        mm = c.get(Calendar.MINUTE)
        ss = c.get(Calendar.SECOND)
        if (closure!=null)
            closure.call(yy, MM, dd, hh, mm, ss)

        [yy, MM, dd, hh, mm, ss]

    }

    Boolean hasTime() {
        def c = this.toCalendar()
        c.equals(c.clearTime())
    }

    @Override
    String toString() {
//        String fmtStr = hasTime() ? 'dd.MM.yy HH:mm:ss' : 'dd.MM.yyyy'
        String fmtStr = 'dd.MM.yy HH:mm:ss'
        this.format(fmtStr)
    }
}