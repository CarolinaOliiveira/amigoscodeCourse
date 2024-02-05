import { useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

const ProtectedRoute = ({ children }) =>{


    const {isCustomerAuthenticated} = useAuth()

    const navigate = useNavigate()
    //useEffect é um hook do react que executa semque que uma pagina é carregada e verifica se o utilizador está logged in
    useEffect(() => {
        if(!isCustomerAuthenticated()){
            navigate("/")
        }
    })

    return isCustomerAuthenticated() ? children : ""
}

export default ProtectedRoute