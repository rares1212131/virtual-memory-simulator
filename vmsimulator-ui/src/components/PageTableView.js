import React, { useState } from 'react';

// Helper function to render the bits in a consistent, readable way
const renderPteBits = (pte) => {
  // Renders as: V D R W
  return (
    <span className="font-monospace">
      {pte.validBit ? '1' : '0'}{' '}
      {pte.dirtyBit ? '1' : '0'}{' '}
      {pte.referenceBit ? '1' : '0'}{' '}
      {pte.writeBit ? '1' : '0'}
    </span>
  );
};

function PageTableView({ pageTables }) {
  // This state keeps track of which first-level entry the user has clicked
  const [selectedPdi, setSelectedPdi] = useState(0);

  // Find the currently selected second-level page table based on the state
  const selectedPageTable = pageTables[selectedPdi];

  return (
    <div className="card">
      <div className="card-header">Page Table (Two-Level)</div>
      <div className="card-body page-table-view-container">
        {/* ---- LEVEL 1: Page Directory (Left Side) ---- */}
        <div className="page-directory">
          <h6 className="text-center">L1: Page Directory</h6>
          {pageTables.map((pt, index) => (
            <button
              key={index}
              onClick={() => setSelectedPdi(index)}
              className={`btn btn-sm d-block w-100 mb-1 ${selectedPdi === index ? 'btn-primary' : (pt.allocated ? 'btn-outline-primary' : 'btn-secondary')}`}
              disabled={!pt.allocated}
            >
              PDI: {index} {pt.allocated ? '(Active)' : '(Unused)'}
            </button>
          ))}
        </div>

        {/* ---- LEVEL 2: Page Table (Right Side) ---- */}
        <div className="page-table">
          {selectedPageTable && selectedPageTable.allocated ? (
            <>
              <h6 className="text-center">L2: Page Table for PDI {selectedPdi}</h6>
              <table className="table table-sm table-bordered">
                <thead>
                  <tr>
                    <th>PTI</th>
                    <th>Bits (VDRW)</th>
                    <th>PPN / Swap#</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedPageTable.entries.map((pte, index) => (
                    <tr key={index} className={pte.validBit ? 'table-success' : ''}>
                      <td>{index}</td>
                      <td>{renderPteBits(pte)}</td>
                      <td>
                        {pte.validBit
                          ? `Frame: ${pte.physicalPageNumber}`
                          : (pte.allocated ? `Disk: ${pte.swapSpaceNumber}`: '-')}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </>
          ) : (
            <div className="text-center text-muted mt-5">
              Select an active Page Directory Entry (PDI) to view its second-level table.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default PageTableView;