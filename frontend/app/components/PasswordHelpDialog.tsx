import { AccessRecoveryDialog } from "./AccessRecoveryDialog";

export function PasswordHelpDialog({ onClose }: { onClose: () => void }) {
  return <AccessRecoveryDialog mode="password" onClose={onClose} />;
}
