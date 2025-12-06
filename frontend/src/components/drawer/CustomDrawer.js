import * as React from 'react';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import IconButton from '@mui/material/IconButton';
import NotificationsIcon from '@mui/icons-material/Notifications';
import Tooltip from '@mui/material/Tooltip';
import styles from "./drawer.module.css";


export default function CustomDrawer() {
  const [open, setOpen] = React.useState(false);

  const toggleDrawer = (newOpen) => () => {
    setOpen(newOpen);
  };

  const DrawerList = (
    <Box style={{ width: "40vh", height: "100vh", background: "#171717"}} role="presentation" onClick={toggleDrawer(false)}>
    </Box>
  );

  return (
    <div>
      <Tooltip title="Показать уведомления">
                <IconButton onClick={toggleDrawer(true)} aria-label="notifications" style={{width: "45px", height: "45px", border: "1px solid #2e2e2e"}}>
                    <NotificationsIcon className={styles.icon} style={{ width: "25px", height: "25px", transition: "all 0.2s ease-in-out"}}/>
                </IconButton>
        </Tooltip>
      <Drawer open={open} onClose={toggleDrawer(false)} anchor='right'>
        {DrawerList}
      </Drawer>
    </div>
  );
}