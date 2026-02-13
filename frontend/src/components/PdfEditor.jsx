import React, { useState } from 'react';
import axios from 'axios';
import { Container, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

const PdfEditor = () => {
    const [file, setFile] = useState(null);
    const [edits, setEdits] = useState({
        name: '',
        gmail: '',
        number: '',
        summary: '',
        education: '',
        skills: '',
        projects: '',
        experience: '',
        certificates: '',
        languages: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile && selectedFile.type === 'application/pdf') {
            setFile(selectedFile);
            setError('');
        } else {
            setError('Please select a valid PDF file.');
            setFile(null);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setEdits({ ...edits, [name]: value });
    };

    const validateForm = () => {
        if (!file) return 'Please select a PDF file.';
        if (!edits.name.trim()) return 'Name is required.';
        if (!edits.gmail.trim()) return 'Gmail is required.';
        if (!edits.number.trim()) return 'Number is required.';
        if (!edits.summary.trim()) return 'Summary is required.';
        if (!edits.education.trim()) return 'Education is required.';
        if (!edits.skills.trim()) return 'Skills are required.';
        if (!edits.projects.trim()) return 'Projects are required.';
        if (!edits.experience.trim()) return 'Experience is required (Min 5 lines for assignment).';
        if (!edits.certificates.trim()) return 'Certificates are required.';
        if (!edits.languages.trim()) return 'Languages are required.';
        return null;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const validationError = validateForm();
        if (validationError) {
            setError(validationError);
            return;
        }

        setLoading(true);
        setError('');
        setSuccess(false);

        try {
            const formData = new FormData();
            formData.append('file', file);

            // Updated mapping: Direct field mapping to backend (no additionalText combination)
            const backendEdits = {
                newExperience: edits.experience,
                modifiedSkill: edits.skills,
                newCertification: edits.certificates,
                name: edits.name,
                gmail: edits.gmail,
                number: edits.number,
                summary: edits.summary,
                education: edits.education,
                projects: edits.projects,
                languages: edits.languages,
            };

            const jsonBlob = new Blob([JSON.stringify(backendEdits)], {
                type: 'application/json'
            });
            formData.append('request', jsonBlob);

            const response = await axios.post('http://localhost:8080/api/pdf/edit', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
                responseType: 'blob',
            });

            // Download Logic
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `updated_${file.name}`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);

            setSuccess(true);
        } catch (err) {
            console.error('Error details:', err);
            setError('Backend Error: Make sure your Spring Boot server is running on port 8080.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-5 mb-5" style={{ maxWidth: '900px' }}>
            <div className="p-4 shadow-sm border rounded bg-white" style={{ fontFamily: 'Arial, sans-serif', lineHeight: '1.6' }}>
                <h2 className="text-center text-primary mb-4">ATS-Friendly Resume Builder</h2>
                <Alert variant="info" className="text-center">
                    Build a professional, ATS-friendly resume. Fill in the sections below and download your updated PDF.
                </Alert>

                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-4">
                        <Form.Label className="fw-bold">Upload Resume (PDF)</Form.Label>
                        <Form.Control type="file" accept=".pdf" onChange={handleFileChange} />
                    </Form.Group>

                    {/* Resume Preview Style */}
                    <div style={{ border: '1px solid #ddd', padding: '20px', backgroundColor: '#f9f9f9' }}>
                        {/* Header */}
                        <div className="text-center mb-4" style={{ borderBottom: '2px solid #007bff', paddingBottom: '10px' }}>
                            <Form.Control
                                type="text"
                                name="name"
                                value={edits.name}
                                onChange={handleInputChange}
                                placeholder="Your Full Name"
                                className="text-center fw-bold fs-4 border-0 bg-transparent"
                                style={{ fontSize: '24px' }}
                            />
                            <Row className="justify-content-center mt-2">
                                <Col md={4}>
                                    <Form.Control
                                        type="email"
                                        name="gmail"
                                        value={edits.gmail}
                                        onChange={handleInputChange}
                                        placeholder="your.email@gmail.com"
                                        className="text-center border-0 bg-transparent"
                                    />
                                </Col>
                                <Col md={4}>
                                    <Form.Control
                                        type="text"
                                        name="number"
                                        value={edits.number}
                                        onChange={handleInputChange}
                                        placeholder="+1-123-456-7890"
                                        className="text-center border-0 bg-transparent"
                                    />
                                </Col>
                            </Row>
                        </div>

                        {/* Sections */}
                        <div className="mb-3">
                            <h5 className="fw-bold text-uppercase" style={{ color: '#007bff', marginBottom: '10px' }}>Summary</h5>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="summary"
                                value={edits.summary}
                                onChange={handleInputChange}
                                placeholder="Brief professional summary..."
                                className="border-0 bg-transparent"
                                style={{ lineHeight: '1.5' }}
                            />
                        </div>

                        <div className="mb-3">
                            <h5 className="fw-bold text-uppercase" style={{ color: '#007bff', marginBottom: '10px' }}>Education</h5>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="education"
                                value={edits.education}
                                onChange={handleInputChange}
                                placeholder="Degree, University, Year..."
                                className="border-0 bg-transparent"
                                style={{ lineHeight: '1.5' }}
                            />
                        </div>

                        <div className="mb-3">
                            <h5 className="fw-bold text-uppercase" style={{ color: '#007bff', marginBottom: '10px' }}>Skills</h5>
                            <Form.Control
                                as="textarea"
                                rows={2}
                                name="skills"
                                value={edits.skills}
                                onChange={handleInputChange}
                                placeholder="e.g. Java, AWS, React (comma-separated)"
                                className="border-0 bg-transparent"
                                style={{ lineHeight: '1.5' }}
                            />
                        </div>

                        <div className="mb-3">
                            <h5 className="fw-bold text-uppercase" style={{ color: '#007bff', marginBottom: '10px' }}>Projects</h5>
                            <Form.Control
                                as="textarea"
                                rows={4}
                                name="projects"
                                value={edits.projects}
                                onChange={handleInputChange}
                                placeholder="Project details..."
                                className="border-0 bg-transparent"
                                style={{ lineHeight: '1.5' }}
                            />
                        </div>

                        <div className="mb-3">
                            <h5 className="fw-bold text-uppercase" style={{ color: '#007bff', marginBottom: '10px' }}>Experience</h5>
                            <Form.Control
                                as="textarea"
                                rows={6}
                                name="experience"
                                value={edits.experience}
                                onChange={handleInputChange}
                                placeholder="Enter 10-12 lines of professional experience..."
                                className="border-0 bg-transparent"
                                style={{ lineHeight: '1.5' }}
                            />
                            <Form.Text className="text-muted">Use \n for new lines.</Form.Text>
                        </div>

                        <div className="mb-3">
                            <h5 className="fw-bold text-uppercase" style={{ color: '#007bff', marginBottom: '10px' }}>Certificates</h5>
                            <Form.Control
                                as="textarea"
                                rows={2}
                                name="certificates"
                                value={edits.certificates}
                                onChange={handleInputChange}
                                placeholder="e.g. AWS Certified, PMP"
                                className="border-0 bg-transparent"
                                style={{ lineHeight: '1.5' }}
                            />
                        </div>

                        <div className="mb-3">
                            <h5 className="fw-bold text-uppercase" style={{ color: '#007bff', marginBottom: '10px' }}>Languages</h5>
                            <Form.Control
                                as="textarea"
                                rows={2}
                                name="languages"
                                value={edits.languages}
                                onChange={handleInputChange}
                                placeholder="e.g. English (Fluent), Hindi (Native)"
                                className="border-0 bg-transparent"
                                style={{ lineHeight: '1.5' }}
                            />
                        </div>
                    </div>

                    <Button variant="primary" type="submit" className="w-100 mt-4 py-2" disabled={loading}>
                        {loading ? (
                            <><Spinner animation="border" size="sm" /> Generating ATS-Friendly Resume...</>
                        ) : (
                            'Update & Download PDF'
                        )}
                    </Button>
                </Form>

                {error && <Alert variant="danger" className="mt-3">{error}</Alert>}
                {success && <Alert variant="success" className="mt-3">Resume Updated! PDF Downloaded.</Alert>}
            </div>
        </Container>
    );
};

export default PdfEditor;