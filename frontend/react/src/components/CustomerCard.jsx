import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    Button,
    useColorModeValue,
    Tag,
    useDisclosure,
    AlertDialog,
    AlertDialogBody,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogContent,
    AlertDialogOverlay,
    AlertDialogCloseButton
} from '@chakra-ui/react'
import { useRef } from 'react'
import { deleteCustomer } from '../services/client.js'
import { successNotification, errorNotification } from '../services/notification.js'
import UpdateCustomerDrawer from './UpdateCustomerDrawer.jsx'


export default function CardWithImage({id, name, email, age, gender, imageNumber, fetchCustomers}) {

    const randomUserGender = gender==="MALE" ? "men" : "women"

    const { isOpen, onOpen, onClose } = useDisclosure()
    const cancelRef = useRef()

    return (
        <Center py={6}>
            <Box
                maxW={'280px'}
                minW={'280px'}
                w={'full'}
                m={2}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                    }
                    objectFit="cover"
                    alt="#"
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/${randomUserGender}/${imageNumber}.jpg`
                        }
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Tag borderRadius='full' colorScheme='cyan'>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age {age} | {gender}</Text>
                    </Stack>
                </Box>
                <Stack direction={'row'} justify={'center'} spacing={3} p={4}>
                    <Stack>
                        <UpdateCustomerDrawer
                            initialValues={{name, email, age}}
                            fetchCustomers={fetchCustomers}
                            CustomerId={id}
                        />
                    </Stack>

                    <Stack>
                        <Button colorScheme={'red'}
                                variant='outline'
                                rounded={'full'}
                                _hover={{
                                    transform: 'translateY(-2px)',
                                    boxShadow: 'lg'}}
                                _focus={{
                                    bg:'grey:500'
                                }}
                                onClick={onOpen}>
                            Delete
                        </Button>

                        <AlertDialog
                            isOpen={isOpen}
                            leastDestructiveRef={cancelRef}
                            onClose={onClose}
                        >
                            <AlertDialogOverlay>
                                <AlertDialogContent>
                                    <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                        Delete Customer
                                    </AlertDialogHeader>

                                    <AlertDialogBody>
                                        Are you  you want to delete {name}? You can't undo this action afterwards.
                                    </AlertDialogBody>

                                    <AlertDialogFooter>
                                        <Button ref={cancelRef} onClick={onClose}>
                                            Cancel
                                        </Button>
                                        <Button colorScheme='red' onClick={ () => {
                                            console.log(id)
                                            deleteCustomer(id)
                                                .then(res => {
                                                    fetchCustomers()
                                                    console.log(res)
                                                    successNotification(
                                                        'Costumer deleted',
                                                        `${name} was successfully deleted`
                                                    )
                                                }).catch(err => {
                                                console.log(err)
                                                errorNotification(
                                                    err.code,
                                                    err.response.data.message
                                                )
                                            }).finally(() => {
                                                onClose()
                                            })
                                        }} ml={3}>
                                            Delete
                                        </Button>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialogOverlay>
                        </AlertDialog>
                    </Stack>
                </Stack>


            </Box>
        </Center>
    )
}