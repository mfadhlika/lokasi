import type { JwtPayload } from "jwt-decode";

type Claim = JwtPayload & {
    username?: string
};

export type { Claim };
