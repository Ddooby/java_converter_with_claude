import os
import anthropic
from dotenv import load_dotenv

load_dotenv()


class ClaudeClient:
    def __init__(self):
        self.client = anthropic.Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])
        self.model = os.getenv("CLAUDE_MODEL", "claude-sonnet-4-6")

    def send(self, system_prompt: str, user_prompt: str, max_tokens: int = 8096) -> str:
        response = self.client.messages.create(
            model=self.model,
            max_tokens=max_tokens,
            system=system_prompt,
            messages=[{"role": "user", "content": user_prompt}],
        )
        return response.content[0].text
