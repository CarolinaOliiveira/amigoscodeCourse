
import {
    Button,
    Drawer,
    DrawerBody,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    DrawerContent,
    DrawerCloseButton,
    useDisclosure
} from '@chakra-ui/react'
import UpdateCustomerForm from './UpdateCustomerForm.jsx'

const AddIcon = () =>  "+"
const CloseIcon = () =>  "x"

const UpdateCustomerDrawer = ({initialValues, fetchCustomers, CustomerId}) => {

    const { isOpen, onOpen, onClose } = useDisclosure()

    return <>
        <Button colorScheme={'cyan'}
                rounded={'full'}
                variant='outline'
                _hover={{
                    transform: 'translateY(-2px)',
                    boxShadow: 'lg'}}
                onClick={onOpen} >
            Update customer
        </Button>

        <Drawer isOpen={isOpen} onClose={onClose} size={'xl'}>
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Update customer</DrawerHeader>

                <DrawerBody>
                    <UpdateCustomerForm
                        initialValues={initialValues}
                        fetchCustomers={ fetchCustomers }
                        CustomerId={CustomerId}

                    />
                </DrawerBody>

                <DrawerFooter>
                    <Button leftIcon={<CloseIcon/>} colorScheme={'cyan'} size='sm' variant='outline' onClick={onClose} >
                        Close
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    </>
}

export default UpdateCustomerDrawer

