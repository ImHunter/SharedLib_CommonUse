package Jenkins.Common

class FolderTest extends GroovyTestCase {

    Folder folder = new Folder('\\\\IBM-FLEX14\\trade_2014')

    void testHasFiles() {
        assertTrue(folder.hasFiles('*.bak'))
    }

    void testHasFiles1() {
        DateNow dt = new DateNow()
        def minDt = dt.getSafeDate('21:00', '20:00', -20)
        println(minDt)
        assertTrue(folder.hasFiles('*.bak', minDt, dt.addMinutes(-1)))
    }
}
