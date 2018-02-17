
def call(){

    def vars = System.getenv()
    echo "$vars.get("APPDATA")"

}
