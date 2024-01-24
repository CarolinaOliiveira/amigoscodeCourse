import UserProfile from './UserProfile.jsx'
import { useState, useEffect } from 'react'

const users = [
    {
        name: "Jamila",
        age: 22,
        gender: "FEMALE"
    },
    {
        name: "Ana",
        age: 45,
        gender: "FEMALE"
    },
    {
        name: "Alex",
        age: 18,
        gender: "MALE"
    },
    {
        name: "Bilal",
        age: 27,
        gender: "MALE"
    }
]

const UserProfiles = ( {users}) => (
    <div>
        < UserProfile
            name={"Hanna"}
            age={23}
            imageNumber={48}
            gender={"women"}>
            <p>Hello</p>
        </UserProfile>
        {users.map((user, index) => (
            <UserProfile
                key = {index}
                name = {user.name}
                age = {user.age}
                gender = {user.gender}
                imageNumber={index}
            />
        ))}
    </div>
)


function App() {

    const [counter, setCounter] = useState(0)
    const [isLoading, setIsLoading] = useState(false)

    // acontece antes do render da página
    //é utilizado para sincronizar com sistemas externos como APIs, para por exemplo ir buscar dados a uma API antes da página aparecer ao utilizador
    useEffect( () => {
        setIsLoading(true)
        setTimeout(() => {
            setIsLoading(false)
        }, 4000)
    }, [])

    if(isLoading) {
        return "Loading ..."
    }


    return (

        <div>
            <button onClick={() => setCounter(counter + 1)}>
                Increment counter
            </button>
            <h1>{ counter }</h1>
            <UserProfiles users = {users}/>
        </div>

    )

}

export default App
