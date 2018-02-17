//import org.apache.ivy.util.FileUtil

import java.nio.file.Paths
import Energos.Jenkins.Common.Dir
import static org.apache.ivy.util.FileUtil.forceDelete

def call(){

    def vars = System.getenv()
    String[] dirs = [vars.get('APPDATA'), vars.get('LOCALAPPDATA')]
    def cachesPath

    dirs.each {String envDir ->
        cachesPath = Paths.get(envDir, '1C', '1cv8').toString()
        new Dir(cachesPath).findDirs('????????-????-????-????-????????????').each { def deletionDir ->
            forceDelete(deletionDir)
        }
    }
}
