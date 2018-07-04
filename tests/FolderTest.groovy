package Jenkins.Common

class FolderTest extends GroovyTestCase {

    Folder folder = new Folder('\\\\IBM-FLEX14\\trade_2014')

    void testHasFiles() {
        assertTrue(folder.hasFiles('*.bak'))
    }

    void testHasFilesAllParams() {
        DateNow dt = new DateNow()
        def minDt = dt.getSafeDate('21:00', '20:00', -20)
        println(minDt)
        assertTrue(folder.hasFiles('*.bak', minDt, dt.addMinutes(-1)))
    }

    void  testDirEnabled(){
        def fld = new Folder('c:\\')
        assertTrue('Проверка доступности каталога', fld.enabled())
    }

    void  testLeaveUnique(){
        def fld = new Folder('E:\\testUniqueFiles')
        fld.leaveLastUniqueFiles('*.*')
        assertTrue('Проверка удаления', true)
    }

    void  testDatedFilename(){
        def f = new Folder('e:\\')
        def res = f.getDatedFileName(true, 'bf_')
        print(res)
        assertTrue('Проверка удаления', true)
    }

}
