import { PublicEnv } from '../../../public-env';
import { Roboto } from "next/font/google";
import "../globals.css";
import DashboardClientProviders from "@/components/providers/DashboardClientProviders";

const roboto = Roboto({
  weight: "400",
  subsets: ["latin"]
});

export const metadata = {
  title: "Finance App",
  description: "Finance App For Personal Use"
};

export default function DashboardLayout({ children }) {
  return (
    <html lang="en">
      <body style={{ fontFamily: roboto.style.fontFamily, height: "100vh" }}>
        <PublicEnv />
        <DashboardClientProviders>
          {children}
        </DashboardClientProviders>
      </body>
    </html>
  );
}
