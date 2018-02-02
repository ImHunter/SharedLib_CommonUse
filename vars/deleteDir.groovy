def call(def dir, Boolean hideException = true){
    try {
        dir.deleteDir();
    } catch (e) {
        if (hideException==false)
            throw (e);
    }
}