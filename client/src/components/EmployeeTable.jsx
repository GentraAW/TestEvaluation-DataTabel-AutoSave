import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import debounce from "lodash/debounce";

const EmployeeTable = () => {
  const [employees, setEmployees] = useState([]);
  const [updatedEmployees, setUpdatedEmployees] = useState([]);
  const [error, setError] = useState("");
  const [successCreate, setSuccessCreate] = useState("");
  const [successUpdate, setSuccessUpdate] = useState("");

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/employee")
      .then((response) => {
        const employeesWithOperation = response.data.map((employee) => ({
          ...employee,
          operation: "update",
          modified: false, // Inisialisasi status modified
        }));
        setEmployees(employeesWithOperation);
        setUpdatedEmployees(employeesWithOperation);
      })
      .catch((error) => {
        if (error.response && error.response.status === 404) {
          setEmployees([]);
          setUpdatedEmployees([]);
        } else {
          setError("Fetching error " + error.message);
        }
      });
  }, []);

  const debouncedSaveChanges = useCallback(
    debounce(() => {
      const dataToSave = updatedEmployees.filter(
        (employee) => employee.modified && employee.operation !== "create"
      );
      if (dataToSave.length > 0) {
        axios
          .post("http://localhost:8080/api/employee/bulk", dataToSave)
          .then((response) => {
            console.log("Employee berhasil update", response);
            setSuccessUpdate("Update Data Success!");
            setError(""); // Clean message error saat berhasil
            // Reset status modified
            const resetModified = updatedEmployees.map((employee) => ({
              ...employee,
              modified: false,
            }));
            setUpdatedEmployees(resetModified);
          })
          .catch((error) => {
            setError("Error update data " + error.message);
            setSuccessUpdate(""); // Clean message sukses saat terjadi error
          });
      }
    }, 1500), // Tunggu 1,5 detik setelah user berhenti mengetik
    [updatedEmployees]
  );

  const handleInputChange = (index, event) => {
    const { name, value } = event.target;
    const updated = [...updatedEmployees];
    updated[index][name] = value;
    updated[index].modified = true; // Tandai sebagai modified
    setUpdatedEmployees(updated);
    debouncedSaveChanges(); // Panggil autosave
  };

  const handleAddRow = () => {
    setUpdatedEmployees([
      ...updatedEmployees,
      {
        id: null,
        name: "",
        age: "",
        email: "",
        phone: "",
        operation: "create",
        modified: true, // Baris baru ditandai sebagai modified
      },
    ]);
    setSuccessUpdate(""); // Bersihkan message success update saat menambah baris
    setSuccessCreate("");
    setError(""); // Bersihkan message error saat menambah baris
  };

  const handleDeleteRow = (index) => {
    const updated = [...updatedEmployees];
    if (updated[index].operation === "create") {
      updated.splice(index, 1);
    } else {
      updated[index].operation = "delete";
      updated[index].modified = true; // Tandai sebagai modified
    }
    setUpdatedEmployees(updated);
    debouncedSaveChanges(); // Panggil autosave
  };

  const handleSaveNewRows = () => {
    const newRows = updatedEmployees.filter(
      (employee) => employee.operation === "create"
    );
    if (newRows.length > 0) {
      axios
        .post("http://localhost:8080/api/employee/bulk", newRows)
        .then((response) => {
          console.log("New rows berhasil disimpan", response);
          setSuccessCreate("Save New Data Success!");
          setError(""); // Clean message error saat berhasil
          // Reset status modified
          const resetModified = updatedEmployees.map((employee) =>
            employee.operation === "create"
              ? { ...employee, operation: "update", modified: false }
              : employee
          );
          setUpdatedEmployees(resetModified);
        })
        .catch((error) => {
          setError("Error saving new rows " + error.message);
          setSuccessCreate(""); // Clean message success saat terjadi error
        });
    }
  };

  return (
    <div className="container p-4 mx-auto">
      {error && <div className="mb-4 text-red-500">{error}</div>}
      {successCreate && (
        <div className="mb-4 text-green-500">{successCreate}</div>
      )}
      {successUpdate && (
        <div className="mb-4 text-green-500">{successUpdate}</div>
      )}
      <table className="w-full table-auto ">
        <thead>
          <tr className="bg-gray-200">
            <th className="p-2 border-2 border-black">ID</th>
            <th className="p-2 border-2 border-black">Name</th>
            <th className="p-2 border-2 border-black">Age</th>
            <th className="p-2 border-2 border-black">Email</th>
            <th className="p-2 border-2 border-black">Phone</th>
            <th className="p-2 border-2 border-black">Actions</th>
          </tr>
        </thead>
        <tbody>
          {updatedEmployees.map((employee, index) => (
            <tr key={employee.id || index}>
              <td className="p-2 border-2 border-black">{employee.id}</td>
              <td className="p-2 border-2 border-black">
                <input
                  type="text"
                  name="name"
                  value={employee.name}
                  onChange={(e) => handleInputChange(index, e)}
                  className="w-full p-1 "
                />
              </td>
              <td className="p-2 border-2 border-black">
                <input
                  type="number"
                  name="age"
                  value={employee.age}
                  onChange={(e) => handleInputChange(index, e)}
                  className="w-full p-1"
                />
              </td>
              <td className="p-2 border-2 border-black">
                <input
                  type="email"
                  name="email"
                  value={employee.email}
                  onChange={(e) => handleInputChange(index, e)}
                  className="w-full p-1"
                />
              </td>
              <td className="p-2 border-2 border-black">
                <input
                  type="tel"
                  name="phone"
                  value={employee.phone}
                  onChange={(e) => handleInputChange(index, e)}
                  className="w-full p-1"
                />
              </td>
              <td className="p-2 border-2 border-black ">
                <div className="flex justify-center">
                  <button
                    onClick={() => handleDeleteRow(index)}
                    className="px-2 py-1 text-white bg-red-500 rounded "
                  >
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <button
        onClick={handleAddRow}
        className="px-4 py-2 mt-4 mr-2 text-white bg-green-500 rounded"
      >
        + Add Row
      </button>
      <button
        onClick={handleSaveNewRows}
        className="px-4 py-2 mt-4 text-white bg-blue-500 rounded"
      >
        Save New Rows
      </button>
    </div>
  );
};

export default EmployeeTable;
