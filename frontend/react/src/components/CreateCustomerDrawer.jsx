
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
import CreateCustomerForm from './shared/CreateCustomerForm.jsx'

const AddIcon = () =>  "+"
const CloseIcon = () =>  "x"

const CreateCustomerDrawer = ({fetchCustomers}) => {

    const { isOpen, onOpen, onClose } = useDisclosure()

    return <>
        <Button leftIcon={<AddIcon/>} colorScheme={'cyan'} size='sm' variant='outline' onClick={onOpen} >
            Create customer
        </Button>
        <Drawer isOpen={isOpen} onClose={onClose} size={'xl'}>
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Create new customer</DrawerHeader>

                <DrawerBody>
                    <CreateCustomerForm
                        onSuccess={ fetchCustomers }
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

export default CreateCustomerDrawer

