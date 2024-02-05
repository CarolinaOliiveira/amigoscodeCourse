import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react"
import { login as performLogin } from "../../services/client"
import { jwtDecode } from "jwt-decode"



const AuthContext = createContext({})

const AuthProvider = ({ children }) => {

    const [customer, setCustomer] = useState(null)


    const setCustomerFromToken = () => {
        //usamos o let para o token ser mutavel
        let token = localStorage.getItem("access_token")
        if(token) {
            token = jwtDecode(token)
            setCustomer({
                username: token.sub,
                roles: token.scopes
            })

        }
    }
    useEffect( () => {
        setCustomerFromToken()
    }, [])



    const login = async(usernameAndPassword) => {
        return new Promise((resolve, reject) => {
            performLogin(usernameAndPassword).then(res => { //no caso do login ser bem sucedido ...
                const jwtToken = res.headers["authorization"]
                //guardar o token
                localStorage.setItem("access_token", jwtToken)
                console.log(jwtToken)
                const decodedToken = jwtDecode(jwtToken)

                //guardar as informações do customer que fez login
                setCustomer({
                    username: decodedToken.sub,
                    roles: decodedToken.scopes
                })
                resolve(res)
            }).catch(err => {
                reject(err)
            })
            
        })
    }

    const logOut = () => {
        // para o logout é preciso remover o access token do cliente que fez login e remover o customer que tinhamos guardado
        localStorage.removeItem("access_token")
        setCustomer(null)
    }


    const isCustomerAuthenticated = ( ) => {
        const token = localStorage.getItem("access_token")
        if(!token){ //se n houver o token é pq nao esta autenticado
            return false
        }
        //se houver token é preciso verificar a data de expiração
        const {exp: expiration} = jwtDecode(token)
        
        if(Date.now() > expiration * 1000){
            logOut()
            return false
        }
        
        return true
    }

    return (
        <AuthContext.Provider value = {{
            customer, 
            login,
            logOut,
            isCustomerAuthenticated,
            setCustomerFromToken
        }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext)
export default AuthProvider
