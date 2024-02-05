import { FormLabel, Input, Alert, AlertIcon, Select, Box, Stack, Button} from '@chakra-ui/react'
import { Formik, Form, useField } from 'formik';
import * as Yup from 'yup'; //permite validar o form
import { saveCustomer} from '../../services/client.js'
import { successNotification, errorNotification } from '../../services/notification.js'

const MyTextInput = ({ label, ...props }) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={'error'} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({ label, ...props }) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={'error'} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = ({ onSuccess }) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    password:'',
                    gender: 'change-Me',

                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(16, "Must be at least 16 years of age")
                        .max(100, "Must be less than 100 years of age")
                        .required(),
                    password: Yup.string()
                        .min(4, "Must be at least 4 characters or more")
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('Required'),
                })}
                onSubmit={(customer, { setSubmitting }) => { //customer = ao customer que vem do form
                    setSubmitting(true)
                    saveCustomer(customer)
                        .then(res => {
                            console.log(res)
                            successNotification(
                                "Customer saved",
                                `${customer.name} was successfully saved`
                            )
                            onSuccess(res.headers["authorization"]) //se a ff fetchCustomers tiver sido passada, voltar a ir buscar os customers à base de dados para a pagina inicial aparecer atualizada
                        }).catch(err => {
                            console.log(err)
                        errorNotification(
                            err.code,
                            err.response.data.message
                        )
                    }).finally(() => {
                        setSubmitting(false)
                    })
                }}
            >
                {({ isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={'24px'}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Type your name"
                            />

                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                                placeholder="Type your email"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="Type your age"
                            />

                            <MyTextInput
                                label="Password"
                                name="password"
                                type="password"
                                placeholder="Type your password"
                            />

                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>

                            <Button type="submit" colorScheme={'cyan'} color='white' isDisabled={!isValid || isSubmitting}>Submit</Button>

                        </Stack>
                    </Form>
                )}

            </Formik>
        </>
    );
};

export default CreateCustomerForm