def call(def envKey, def withAssert = true){

    def res = env[envKey]
    if (withAssert==true)
        assert res != null

    res

}