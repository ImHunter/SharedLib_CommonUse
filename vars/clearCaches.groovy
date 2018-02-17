
def call(){

    def vars = System.getenv()
    def dirs = [vars.get('APPDATA'), vars.get('LOCALAPPDATA')]
    echo "${dirs}"

}
