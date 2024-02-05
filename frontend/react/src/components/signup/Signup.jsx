import {
    Button,
    Checkbox,
    Box,
    Alert,
    AlertIcon,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image,
  } from '@chakra-ui/react'
  import { Link, useNavigate } from 'react-router-dom'
  import { useAuth } from '../context/AuthContext';
  import { errorNotification } from '../../services/notification';
  import { useEffect } from 'react';
import CreateCustomerForm from '../shared/CreateCustomerForm';


const Signup = () => {
    const { customer, setCustomerFromToken } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    if(customer) {
      navigate("/dashboard")
    }
  })

  return (
    <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
      <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
        <Stack spacing={4} w={'full'} maxW={'md'}>
            <Image 
            src={"https://user-images.githubusercontent.com/40702606/210880158-e7d698c2-b19a-4057-b415-09f48a746753.png"}
            boxSize={'150px'}
            alt='Amigoscode Logo'
            style={{ alignSelf: 'center' }}
            />
          <Heading fontSize={'2xl'} mb={15} mt={15}>Register for an account</Heading>
            <CreateCustomerForm onSuccess={(token) => {
                localStorage.setItem("access_token", token)
                setCustomerFromToken()
                navigate("/dashboard")
                }}/>
            <Text color={'#2B909D'} >
            <Link to={"/"}>
                Already have an account? Login now!
              </Link>
        </Text>
            
        </Stack>
      </Flex>
      <Flex flex={1} 
            padding={10} 
            flexDirection={'column'} 
            alignItems={'center'} 
            justifyContent={'center'} 
            bgGradient={{sm: 'linear(to-r, cyan.600, purple.600)'}}
      >
        <Text fontSize={'6xl'} color={'white'} fontWeight={'bold'} mb={5} >
            <Link to={"https://amigoscode.com/courses"} > 
                Enrol Now
            </Link>
        </Text>
        <Image
          alt={'Login Image'}
          objectFit={'scale-down'}
          src={
            "https://user-images.githubusercontent.com/40702606/215539167-d7006790-b880-4929-83fb-c43fa74f429e.png"
          }
        />
      </Flex>
    </Stack>
  )
}

export default Signup