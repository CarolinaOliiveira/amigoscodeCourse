import { FormLabel, Input, Alert, AlertIcon, Select, Box, Stack, Button} from '@chakra-ui/react'
import { Formik, Form, useField } from 'formik';
import * as Yup from 'yup'; //permite validar o form
import { updateCustomer} from '../services/client.js'
import { successNotification, errorNotification } from '../services/notification.js'

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

// And now we can use these
const UpdateCustomerForm = ({ initialValues, fetchCustomers, CustomerId }) => {
    return (
        <>
            <Formik
                initialValues={initialValues}
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
                })}
                onSubmit={(updatedCustomer, { setSubmitting }) => { //customer = ao customer que vem do form
                    setSubmitting(true)
                    updateCustomer(CustomerId, updatedCustomer)
                        .then(res => {
                            console.log(res)
                            successNotification(
                                "Customer updated",
                                `${updatedCustomer.name} was successfully updated`
                            )
                            fetchCustomers() //voltar a ir buscar os customers à base de dados para a pagina inicial aparecer atualizada
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
                {({ isValid, isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing={'24px'}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Jane"
                            />

                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                                placeholder="jane@formik.com"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="24"
                            />

                            <Button isDisabled={!(isValid && dirty) || isSubmitting} type="submit" colorScheme={'cyan'} color='white'>Submit</Button>

                        </Stack>
                    </Form>
                )}

            </Formik>
        </>
    );
};

export default UpdateCustomerForm