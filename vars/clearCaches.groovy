import java.nio.file.Paths
import Energos.Jenkins.Common.Dir

def call(){

    def vars = System.getenv()
    String[] dirs = [vars.get('APPDATA'), vars.get('LOCALAPPDATA')]
    def del
    echo "${dirs}"

    dirs.each {String envDir ->
        new Dir(Paths.get(envDir, '1C', '1cv8').toString())
                .findDirs('????????-????-????-????-????????????').each { cache ->
            echo "${cache}"
        }
    }

}
