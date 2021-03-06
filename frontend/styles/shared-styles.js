const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="custom-grid" theme-for="vaadin-grid"> 
  <template> 
   <style>
            [part~="header-cell"] {
                background-color: steelblue;
                color: white;
            }

            [part~="header-cell"] ::slotted(vaadin-grid-cell-content), [part~="reorder-ghost"] {
                font-weight: bold;
                font-size: large;
            }

            [part~="footer-cell"] ::slotted(vaadin-grid-cell-content) {
                font-weight: bold;
            }

            :host(:not([theme~="no-row-borders"])) [part="row"]:first-child [part~="footer-cell"] {
                border-top: 2px solid black;
                border-bottom: 2px solid black;
            }
        </style> 
  </template> 
 </dom-module>`;

document.head.appendChild($_documentContainer.content);

